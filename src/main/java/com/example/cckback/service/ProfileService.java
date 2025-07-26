package com.example.cckback.service;

import com.example.cckback.Entity.Technicien;
import com.example.cckback.Entity.Utilisateur;
import com.example.cckback.Repository.UserRepository;
import com.example.cckback.dto.UpdateProfileRequest;
import org.springframework.stereotype.Service;

@Service
public class ProfileService {
    private final UserRepository userRepository;

    public ProfileService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Utilisateur updateUserProfile(Long userId, UpdateProfileRequest request) {
        Utilisateur user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Mise à jour des champs de base
        user.setNom(request.getNom());
        user.setPrenom(request.getPrenom());

        // Mise à jour des champs spécifiques au technicien si c'est un technicien
        if (user instanceof Technicien) {
            ((Technicien) user).setNumeroTelephone(request.getNumeroTelephone());
        }

        return userRepository.save(user);
    }

    public Utilisateur getCurrentUserProfile(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }
}