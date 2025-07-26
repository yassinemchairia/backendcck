package com.example.cckback.controller;

import com.example.cckback.dto.PasswordResetRequest;
import com.example.cckback.service.PasswordResetService;
import jakarta.mail.MessagingException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/password")
public class PasswordResetController {
    private final PasswordResetService passwordResetService;

    public PasswordResetController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    @PostMapping("/reset-request")
    public void requestReset(@RequestParam String email) throws MessagingException {
        passwordResetService.requestPasswordReset(email);
    }

    @PostMapping("/reset")
    public void resetPassword(@RequestBody PasswordResetRequest request) {
        passwordResetService.resetPassword(request);
    }
}