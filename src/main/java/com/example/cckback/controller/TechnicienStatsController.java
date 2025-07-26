package com.example.cckback.controller;

import com.example.cckback.dto.TechnicienStatsDTO;
import com.example.cckback.service.TechnicienStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class TechnicienStatsController {
    @Autowired
    private  TechnicienStatsService statsService;

    @GetMapping("/technicien/{id}")
    public TechnicienStatsDTO getStats(@PathVariable Long id) {
        return statsService.getStatsForTechnicien(id);
    }
}