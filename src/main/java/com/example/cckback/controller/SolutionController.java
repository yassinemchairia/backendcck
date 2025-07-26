package com.example.cckback.controller;

import com.example.cckback.dto.SolutionPredictionRequest;
import com.example.cckback.dto.SolutionPredictionResponse;
import com.example.cckback.service.SolutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/solutions")
public class SolutionController {

    @Autowired
    private SolutionService solutionService;

    @PostMapping("/predict")
    public ResponseEntity<String> predictSolution(@RequestBody SolutionPredictionRequest request) {
        String solution = solutionService.predictSolution(request);
        return ResponseEntity.ok(solution);
    }
}