package com.example.cckback.service;

import com.example.cckback.dto.SolutionPredictionRequest;
import com.example.cckback.dto.SolutionPredictionResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SolutionService {
    @Value("${flask.api.solution.url}")
    private String flaskSolutionApiUrl;

    private final RestTemplate restTemplate;

    public SolutionService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String predictSolution(SolutionPredictionRequest request) {
        try {
            ResponseEntity<SolutionPredictionResponse> response = restTemplate.postForEntity(
                    flaskSolutionApiUrl + "/predict_solution", request, SolutionPredictionResponse.class
            );
            return response.getBody().getSolution();
        } catch (Exception e) {
            e.printStackTrace();
            return "Erreur lors de la prédiction de la solution.";
        }
    }
}
