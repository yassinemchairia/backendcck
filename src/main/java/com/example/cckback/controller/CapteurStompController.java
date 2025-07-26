package com.example.cckback.controller;

import com.example.cckback.Entity.Capteur;
import com.example.cckback.service.SurveillanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
public class CapteurStompController {
    private final SurveillanceService surveillanceService;

    @Autowired
    public CapteurStompController(SurveillanceService surveillanceService) {
        this.surveillanceService = surveillanceService;
    }

    @MessageMapping("/getInitialCapteurs")
    public void handleGetInitialCapteurs() {
        surveillanceService.updateInitialCapteurs();
    }
}
