package com.example.cckback;

import com.example.cckback.dto.SolutionPredictionRequest;
import com.example.cckback.dto.SolutionPredictionResponse;
import com.example.cckback.service.SolutionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SolutionServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private SolutionService solutionService;

    private SolutionPredictionRequest request;
    private SolutionPredictionResponse response;

    @BeforeEach
    void setUp() {
        request = new SolutionPredictionRequest();
        request.setTypePanne("CLIMATISATION");
        request.setNiveauGravite("CRITIQUE");
        request.setValeurDeclenchement(35.5);
        request.setTypeCapteur("TEMPERATURE");
        request.setEmplacement("Salle 101");
        request.setDescription("Température élevée");

        response = new SolutionPredictionResponse();
        response.setSolution("Vérifier le compresseur");

        // Set flaskApiUrl using reflection
        ReflectionTestUtils.setField(solutionService, "flaskApiUrl", "http://localhost:5000");
    }

    @Test
    void predictSolution_SuccessfulResponse_ShouldReturnSolution() {
        when(restTemplate.postForEntity(eq("http://localhost:5000/predict_solution"), eq(request), eq(SolutionPredictionResponse.class)))
                .thenReturn(ResponseEntity.ok(response));

        String result = solutionService.predictSolution(request);

        assertEquals("Vérifier le compresseur", result);
        verify(restTemplate).postForEntity(anyString(), eq(request), eq(SolutionPredictionResponse.class));
    }

    @Test
    void predictSolution_ApiError_ShouldReturnErrorMessage() {
        when(restTemplate.postForEntity(anyString(), eq(request), eq(SolutionPredictionResponse.class)))
                .thenThrow(new RuntimeException("API Error"));

        String result = solutionService.predictSolution(request);

        assertEquals("Erreur lors de la prédiction de la solution.", result);
        verify(restTemplate).postForEntity(anyString(), eq(request), eq(SolutionPredictionResponse.class));
    }
}