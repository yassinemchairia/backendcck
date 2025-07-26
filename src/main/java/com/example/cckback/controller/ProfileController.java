package com.example.cckback.controller;

import com.example.cckback.Entity.Utilisateur;
import com.example.cckback.Repository.UserRepository;
import com.example.cckback.dto.UpdateProfileRequest;
import com.example.cckback.security.jwt.JwtService;
import com.example.cckback.service.ProfileService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {
    private final ProfileService profileService;
    private  final JwtService jwtService;
    private final UserRepository userRepository;


    public ProfileController(ProfileService profileService, JwtService jwtService, UserRepository userRepository) {
        this.profileService = profileService;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @PutMapping("/updateprofile/{userId}")
    public Utilisateur updateProfile(
            @PathVariable Long userId,
            @RequestBody UpdateProfileRequest request
    ) {
        return profileService.updateUserProfile(userId, request);
    }



}