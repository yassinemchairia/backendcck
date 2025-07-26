package com.example.cckback.controller;




import com.example.cckback.dto.StatistiquesPreventionDTO;
import com.example.cckback.service.StatistiqueService;
import com.example.cckback.service.StatistiquesService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/statistiques")
public class StatistiquesController {

    private final StatistiquesService statistiquesService;
    private final StatistiqueService statistiqueService;
    public StatistiquesController(StatistiquesService statistiquesService,StatistiqueService statistiqueService) {
        this.statistiquesService = statistiquesService;
        this.statistiqueService = statistiqueService;
    }
    @GetMapping("/preventions")
    public Map<String, Object> getStatistiquesPrevention() {
        return statistiqueService.getStatistiquesPrevention();
    }
    //@GetMapping("/prevention")
    //public ResponseEntity<StatistiquesPreventionDTO> getStatistiquesPrevention() {
       // return ResponseEntity.ok()
                //.contentType(MediaType.APPLICATION_JSON)
                //.body(statistiquesService.getStatistiquesPrevention());
    //}

}