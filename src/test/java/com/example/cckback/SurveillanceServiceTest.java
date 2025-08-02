package com.example.cckback;

import com.example.cckback.Entity.*;
import com.example.cckback.Repository.CapteurRepository;
import com.example.cckback.service.AlerteService;
import com.example.cckback.service.ConfigService;
import com.example.cckback.service.SurveillanceService;
import org.eclipse.paho.client.mqttv3.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SurveillanceServiceTest {

    @Mock
    private CapteurRepository capteurRepository;

    @Mock
    private AlerteService alerteService;

    @Mock
    private ConfigService configService;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private MqttClient mqttClient;

    @InjectMocks
    private SurveillanceService surveillanceService;

    private Capteur capteur;
    private Alerte alerte;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException, MqttException {
        capteur = new Capteur();
        capteur.setIdCapt(1L);
        capteur.setIpAdresse("192.168.1.1");
        capteur.setType(Capteur.TypeCapteur.TEMPERATURE);
        capteur.setDepartement(Capteur.Departement.MANOUBA);
        capteur.setUniteMesure("°C");

        alerte = new Alerte();
        alerte.setIdAlerte(1L);
        alerte.setCapteur(capteur);
        alerte.setTypePanne(Alerte.TypePanne.ENVIRONNEMENT);
        alerte.setNiveauGravite(Alerte.NiveauGravite.CRITIQUE);
        alerte.setValeurDeclenchement(35.5);

        // Set configuration properties using reflection
        ReflectionTestUtils.setField(surveillanceService, "mqttBrokerUrl", "tcp://localhost:1883");
        ReflectionTestUtils.setField(surveillanceService, "mqttClientId", "test-client");
        ReflectionTestUtils.setField(surveillanceService, "intervalleVerificationMs", 5000L);
        ReflectionTestUtils.setField(surveillanceService, "delaiMinEntreAlertes", 5L);

        // Mock MqttClient creation
        Field mqttClientField = SurveillanceService.class.getDeclaredField("mqttClient");
        mqttClientField.setAccessible(true);
        mqttClientField.set(surveillanceService, mqttClient);
    }


    @Test
    void messageArrived_ValidMessage_ShouldUpdateDerniereValeur() {
        String topic = "sensors/MANOUBA/TEMPERATURE/192.168.1.1";
        MqttMessage message = new MqttMessage("35.5".getBytes());
        when(capteurRepository.findByIpAdresse("192.168.1.1")).thenReturn(Optional.of(capteur));

        surveillanceService.messageArrived(topic, message);

        verify(capteurRepository).findByIpAdresse("192.168.1.1");
        Map<Long, Double> derniereValeur = (Map<Long, Double>) ReflectionTestUtils.getField(surveillanceService, "derniereValeur");
        assertEquals(35.5, derniereValeur.get(capteur.getIdCapt()));
    }

    @Test
    void surveillerCapteurs_AnomalyDetected_ShouldCreateAlerte() {
        ConfigService.SeuilConfig seuils = mock(ConfigService.SeuilConfig.class);
        when(seuils.determinerNiveau(35.5)).thenReturn(Alerte.NiveauGravite.CRITIQUE);
        when(configService.getSeuils(Capteur.TypeCapteur.TEMPERATURE)).thenReturn(seuils);
        when(capteurRepository.findAll()).thenReturn(Collections.singletonList(capteur));
        when(alerteService.existeAlerteNonResoluePourCapteur(capteur, Alerte.NiveauGravite.CRITIQUE)).thenReturn(false);
        when(alerteService.creerAlertes(any(Alerte.class))).thenReturn(alerte);
        when(capteurRepository.saveAll(anyList())).thenReturn(Collections.singletonList(capteur));

        // Set derniereValeur
        Map<Long, Double> derniereValeur = new ConcurrentHashMap<>();
        derniereValeur.put(capteur.getIdCapt(), 35.5);
        ReflectionTestUtils.setField(surveillanceService, "derniereValeur", derniereValeur);

        surveillanceService.surveillerCapteurs();

        verify(alerteService).creerAlertes(any(Alerte.class));
        verify(messagingTemplate).convertAndSend(eq("/topic/capteurs"), eq(capteur));
        verify(messagingTemplate).convertAndSend(eq("/topic/alertes"), any(Alerte.class));
        verify(capteurRepository).saveAll(anyList());
    }

    @Test
    void surveillerCapteurs_NoAnomaly_ShouldNotCreateAlerte() {
        ConfigService.SeuilConfig seuils = mock(ConfigService.SeuilConfig.class);
        when(seuils.determinerNiveau(20.0)).thenReturn(Alerte.NiveauGravite.NORMALE);
        when(configService.getSeuils(Capteur.TypeCapteur.TEMPERATURE)).thenReturn(seuils);
        when(capteurRepository.findAll()).thenReturn(Collections.singletonList(capteur));
        when(capteurRepository.saveAll(anyList())).thenReturn(Collections.singletonList(capteur));

        // Set derniereValeur
        Map<Long, Double> derniereValeur = new ConcurrentHashMap<>();
        derniereValeur.put(capteur.getIdCapt(), 20.0);
        ReflectionTestUtils.setField(surveillanceService, "derniereValeur", derniereValeur);

        surveillanceService.surveillerCapteurs();

        verify(alerteService, never()).creerAlertes(any());
        verify(messagingTemplate).convertAndSend(eq("/topic/capteurs"), eq(capteur));
        verify(capteurRepository).saveAll(anyList());
    }

    @Test
    void peutCreerAlerte_NoRecentAlerte_ShouldReturnTrue() {
        when(alerteService.existeAlerteNonResoluePourCapteur(capteur, Alerte.NiveauGravite.CRITIQUE)).thenReturn(false);

        boolean result = (Boolean) ReflectionTestUtils.invokeMethod(surveillanceService, "peutCreerAlerte", capteur, Alerte.NiveauGravite.CRITIQUE);

        assertTrue(result);
        verify(alerteService).existeAlerteNonResoluePourCapteur(capteur, Alerte.NiveauGravite.CRITIQUE);
    }
}