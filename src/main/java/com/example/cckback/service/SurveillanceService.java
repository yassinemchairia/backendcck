package com.example.cckback.service;

import com.example.cckback.Entity.*;
import com.example.cckback.Repository.CapteurRepository;
import jakarta.annotation.PreDestroy;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SurveillanceService implements MqttCallback {
    private static final Logger logger = LoggerFactory.getLogger(SurveillanceService.class);
    private static final long DELAI_MIN_ENTRE_ALERTES = 5; // minutes

    private final CapteurRepository capteurRepository;
    private final AlerteService alerteService;
    private final ConfigService configService;
    private final SimpMessagingTemplate messagingTemplate;
    private MqttClient mqttClient;
    private final Map<Long, Double> derniereValeur = new ConcurrentHashMap<>();
    private final Map<Long, LocalDateTime> dernierEnvoiAlerte = new ConcurrentHashMap<>();

    @Value("${mqtt.broker.url:tcp://localhost:1883}")
    private String mqttBrokerUrl;

    @Value("${mqtt.client.id:cckback-sensor-client}")
    private String mqttClientId;

    @Value("${surveillance.intervalle-verification:5000}")
    private long intervalleVerificationMs;

    @Value("${surveillance.delai-entre-alertes:5}")
    private long delaiMinEntreAlertes;

    @Autowired
    public SurveillanceService(CapteurRepository capteurRepository,
                               AlerteService alerteService,
                               ConfigService configService,
                               SimpMessagingTemplate messagingTemplate) {
        this.capteurRepository = capteurRepository;
        this.alerteService = alerteService;
        this.configService = configService;
        this.messagingTemplate = messagingTemplate;
    }

    public CapteurRepository getCapteurRepository() {
        return capteurRepository;
    }

    @PostConstruct
    public void init() throws MqttException {
        mqttClient = new MqttClient(mqttBrokerUrl, mqttClientId, new MemoryPersistence());
        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(true);
        mqttClient.connect(options);
        logger.info("Connected to MQTT broker at {}", mqttBrokerUrl);
        subscribeToSensorTopics();
        mqttClient.setCallback(this);
        // Envoyer les capteurs initiaux après connexion
        updateInitialCapteurs();
    }

    @PreDestroy
    public void cleanup() {
        try {
            if (mqttClient != null && mqttClient.isConnected()) {
                mqttClient.disconnect();
                logger.info("Disconnected from MQTT broker");
            }
        } catch (MqttException e) {
            logger.error("Error during cleanup", e);
        }
    }

    private void subscribeToSensorTopics() throws MqttException {
        List<Capteur> capteurs = capteurRepository.findAll();
        for (Capteur capteur : capteurs) {
            String topic = String.format("sensors/%s/%s/%s",
                    capteur.getDepartement(), capteur.getType(), capteur.getIpAdresse());
            mqttClient.subscribe(topic, 1);
            logger.debug("Subscribed to topic: {}", topic);
        }
    }

    @Override
    public void connectionLost(Throwable cause) {
        logger.warn("MQTT connection lost. Attempting to reconnect...", cause);
        try {
            init();
        } catch (MqttException e) {
            logger.error("Reconnection failed", e);
        }
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) {
        try {
            logger.info("Message received on topic: {}", topic);
            String[] parts = topic.split("/");
            String ipAdresse = parts[3];
            Double value = Double.parseDouble(new String(message.getPayload()));
            Capteur capteur = capteurRepository.findByIpAdresse(ipAdresse)
                    .orElseThrow(() -> new IllegalArgumentException("No sensor found for IP: " + ipAdresse));
            derniereValeur.put(capteur.getIdCapt(), value);
            logger.debug("Received MQTT message for sensor {}: {}", capteur.getIdCapt(), value);
        } catch (Exception e) {
            logger.error("Error processing MQTT message for topic {}: {}", topic, e.getMessage(), e);
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        // Not used for subscription
    }

    @Scheduled(fixedRateString = "${surveillance.intervalle-verification:5000}")
    @Transactional
    public void surveillerCapteurs() {
        logger.debug("Début de la surveillance des capteurs");

        try {
            List<Capteur> capteurs = capteurRepository.findAll();
            logger.info("Nombre de capteurs trouvés : {}", capteurs.size());
            List<Capteur> capteursAMettreAJour = new ArrayList<>(capteurs.size());

            capteurs.parallelStream().forEach(capteur -> {
                try {
                    Double valeur = getValeurCapteurEnTempsReel(capteur);
                    logger.info("Capteur {} (IP: {}): valeur reçue = {}",
                            capteur.getIdCapt(), capteur.getIpAdresse(), valeur);
                    capteur.mettreAJourValeur(valeur);
                    logger.info("Capteur {}: valeur_actuelle = {}, etat_electricite = {}, derniereMiseAJour = {}",
                            capteur.getIdCapt(), capteur.getValeurActuelle(),
                            capteur.getEtatElectricite(), capteur.getDerniereMiseAJour());
                    capteursAMettreAJour.add(capteur);

                    evaluerEtatCapteur(capteur, valeur);
                } catch (Exception e) {
                    logger.error("Erreur surveillance capteur {}", capteur.getIdCapt(), e);
                }
            });

            if (!capteursAMettreAJour.isEmpty()) {
                logger.info("Sauvegarde de {} capteurs", capteursAMettreAJour.size());
                capteurRepository.saveAll(capteursAMettreAJour);
                logger.info("Capteurs sauvegardés");
                for (Capteur capteur : capteursAMettreAJour) {
                    sendCapteurUpdate(capteur); // Envoyer mise à jour via STOMP
                }
            }
        } catch (Exception e) {
            logger.error("Erreur générale lors de la surveillance", e);
        }

        logger.debug("Fin de la surveillance des capteurs");
    }

    private void evaluerEtatCapteur(Capteur capteur, Double valeur) {
        Alerte.NiveauGravite niveau = determinerNiveauGravite(capteur.getType(), valeur);

        if (niveau != Alerte.NiveauGravite.NORMALE) {
            traiterAnomalie(capteur, valeur, niveau);
        }
    }

    private void traiterAnomalie(Capteur capteur, Double valeur, Alerte.NiveauGravite niveau) {
        if (peutCreerAlerte(capteur, niveau)) {
            Alerte alerte = construireAlerte(capteur, valeur, niveau);
            alerteService.creerAlertes(alerte);
            dernierEnvoiAlerte.put(capteur.getIdCapt(), LocalDateTime.now());
            logger.info("Alerte créée: capteur={}, valeur={}, gravité={}",
                    capteur.getIdCapt(), valeur, niveau);
            sendAlertUpdate(alerte); // Envoyer alerte via STOMP
        }
    }

    private Alerte construireAlerte(Capteur capteur, Double valeur, Alerte.NiveauGravite niveau) {
        Alerte alerte = new Alerte();
        alerte.setCapteur(capteur);
        alerte.setTypePanne(convertirTypeCapteurEnTypePanne(capteur.getType()));
        alerte.setNiveauGravite(niveau);
        alerte.setValeurDeclenchement(valeur);
        alerte.setDescription(genererDescriptionAlerte(capteur, valeur, niveau));
        return alerte;
    }

    private Double getValeurCapteurEnTempsReel(Capteur capteur) {
        Double valeur = derniereValeur.get(capteur.getIdCapt());
        if (valeur == null) {
            logger.warn("No recent data for sensor {}. Using default value.", capteur.getIdCapt());
            valeur = capteur.getType() == Capteur.TypeCapteur.ELECTRICITE ? 1.0 : 20.0;
            derniereValeur.put(capteur.getIdCapt(), valeur);
        }
        return valeur;
    }

    private boolean peutCreerAlerte(Capteur capteur, Alerte.NiveauGravite niveau) {
        if (alerteService.existeAlerteNonResoluePourCapteur(capteur, niveau)) {
            return false;
        }

        LocalDateTime dernierEnvoi = dernierEnvoiAlerte.get(capteur.getIdCapt());
        return dernierEnvoi == null ||
                dernierEnvoi.plusMinutes(DELAI_MIN_ENTRE_ALERTES).isBefore(LocalDateTime.now());
    }

    private String genererDescriptionAlerte(Capteur capteur, Double valeur, Alerte.NiveauGravite niveau) {
        return String.format("%s anormal détecté: %.2f %s (%s - %s)",
                capteur.getType(),
                valeur,
                getUniteMesure(capteur.getType()),
                niveau,
                capteur.getDepartement());
    }

    private String getUniteMesure(Capteur.TypeCapteur type) {
        return switch (type) {
            case TEMPERATURE -> "°C";
            case HUMIDITE -> "%";
            case ELECTRICITE -> "";
        };
    }

    private Alerte.NiveauGravite determinerNiveauGravite(Capteur.TypeCapteur type, Double valeur) {
        if (valeur == null) return Alerte.NiveauGravite.NORMALE;
        ConfigService.SeuilConfig seuils = configService.getSeuils(type);
        return seuils.determinerNiveau(valeur);
    }

    private Alerte.TypePanne convertirTypeCapteurEnTypePanne(Capteur.TypeCapteur typeCapteur) {
        return switch (typeCapteur) {
            case TEMPERATURE, HUMIDITE -> Alerte.TypePanne.ENVIRONNEMENT;
            case ELECTRICITE -> Alerte.TypePanne.ELECTRICITE;
        };
    }

    // Méthodes pour envoyer des mises à jour via STOMP
    public void sendCapteurUpdate(Capteur capteur) {
        messagingTemplate.convertAndSend("/topic/capteurs", capteur);
        logger.debug("Sent capteur update to /topic/capteurs: {}", capteur.getIdCapt());
    }

    public void sendAlertUpdate(Alerte alerte) {
        messagingTemplate.convertAndSend("/topic/alertes", alerte);
        logger.debug("Sent alerte update to /topic/alertes: {}", alerte.getIdAlerte());
    }

    public void updateInitialCapteurs() {
        List<Capteur> capteurs = capteurRepository.findAll();
        messagingTemplate.convertAndSend("/topic/capteurs", capteurs);
        logger.debug("Sent initial capteurs to /topic/capteurs");
    }
}