package com.example.cckback;

import com.example.cckback.Entity.*;
import com.example.cckback.Repository.AlerteRepository;
import com.example.cckback.dto.AlertessDTO;
import com.example.cckback.dto.Intervention1DTO;
import com.example.cckback.service.AlertePredictionService;
import com.example.cckback.service.AlerteService;
import com.example.cckback.service.InterventionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AlertePredictionServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private AlerteRepository alerteRepository;

    @Mock
    private InterventionService interventionService;

    @Mock
    private AlerteService alerteService;

    @InjectMocks
    private AlertePredictionService alertePredictionService;

    private AlertessDTO alertessDTO;
    private Alerte alerte;
    private Map<String, Object> prediction;

    @BeforeEach
    void setUp() {
        alertessDTO = new AlertessDTO();
        alertessDTO.setTypePanne("MOTEUR");
        alertessDTO.setNiveauGravite(String.valueOf(Alerte.NiveauGravite.CRITIQUE));
        alertessDTO.setValeurDeclenchement(95.5);
        alertessDTO.setTypeCapteur("TEMP");
        alertessDTO.setEmplacement("Salle A");
        alertessDTO.setDescription("Température élevée");

        alerte = new Alerte();
        alerte.setIdAlerte(1L);
        alerte.setNiveauGravite(Alerte.NiveauGravite.CRITIQUE);
        alerte.setTypePanne(Alerte.TypePanne.ELECTRICITE);
        alerte.setValeurDeclenchement(95.5);
        alerte.setDateAlerte(LocalDateTime.now());
        alerte.setEstResolu(false);

        prediction = new HashMap<>();
        prediction.put("solution", "Redémarrer le système");
    }

    @Test
    void testPredictSolution_Success() {
        // Arrange
        ResponseEntity<Map> response = new ResponseEntity<>(prediction, HttpStatus.OK);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(response);

        // Act
        Map<String, Object> result = alertePredictionService.predictSolution(alertessDTO);

        // Assert
        assertNotNull(result);
        assertEquals("Redémarrer le système", result.get("solution"));
        verify(restTemplate).postForEntity(eq("http://localhost:5000/predict_solution"), any(HttpEntity.class), eq(Map.class));
    }

    @Test
    void testPredictSolution_Failure() {
        // Arrange
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Map.class)))
                .thenThrow(new RuntimeException("API Error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> alertePredictionService.predictSolution(alertessDTO));
    }

    @Test
    void testCreateInterventionFromPrediction_Success() {
        // Arrange
        when(alerteService.saveAlerte(any(Alerte.class))).thenReturn(alerte);
        when(interventionService.saveIntervention(any(Intervention.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Intervention intervention = alertePredictionService.createInterventionFromPrediction(alerte, prediction);

        // Assert
        assertNotNull(intervention);
        assertEquals(alerte, intervention.getAlerte());
        assertEquals(Statut.TERMINEE, intervention.getStatut());
        assertEquals(TypeIntervention.CORRECTIVE, intervention.getTypeIntervention());
        assertEquals(PrioriteIntervention.ELEVEE, intervention.getPriorite());
        assertTrue(intervention.isResolvedByAI());
        assertEquals("Redémarrer le système", intervention.getRapport().getDetails());
        assertEquals(5, intervention.getRapport().getSatisfaction());
        assertTrue(alerte.isEstResolu());
        verify(alerteService).saveAlerte(alerte);
        verify(interventionService).saveIntervention(intervention);
    }

    @Test
    void testGetResolvedAIAlerts() {
        // Arrange
        List<Alerte> alerts = Collections.singletonList(alerte);
        when(alerteRepository.findByEstResoluTrueAndInterventionsResolvedByAITrue()).thenReturn(alerts);

        // Act
        List<Alerte> result = alertePredictionService.getResolvedAIAlerts();

        // Assert
        assertEquals(1, result.size());
        assertEquals(alerte, result.get(0));
        verify(alerteRepository).findByEstResoluTrueAndInterventionsResolvedByAITrue();
    }



    @Test
    void testGetInterventionDetailsForAlert_AlertNotFound() {
        // Arrange
        when(alerteRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> alertePredictionService.getInterventionDetailsForAlert(1L));
    }


}