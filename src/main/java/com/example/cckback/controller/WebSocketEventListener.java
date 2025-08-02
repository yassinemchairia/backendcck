package com.example.cckback.controller;

import com.example.cckback.Entity.Utilisateur;
import com.example.cckback.Repository.UserRepository;
import com.example.cckback.service.ConnectedUsersService;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;

@Component
public class WebSocketEventListener {
    private final ConnectedUsersService connectedUsersService;
    private final UserRepository userRepository;

    public WebSocketEventListener(ConnectedUsersService connectedUsersService, UserRepository userRepository) {
        this.connectedUsersService = connectedUsersService;
        this.userRepository = userRepository;
    }

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String userId = headerAccessor.getFirstNativeHeader("userId");
        if (userId != null) {
            Long id = Long.valueOf(userId);
            Utilisateur user = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("User not found: " + userId));
            // Set the user principal to the email
            headerAccessor.setUser(new Principal() {
                @Override
                public String getName() {
                    return user.getEmail();
                }
            });
            connectedUsersService.addUser(id);
            System.out.println("WebSocket connected for user: " + user.getEmail() + ", userId: " + userId);
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String userId = headerAccessor.getFirstNativeHeader("userId");
        if (userId != null) {
            connectedUsersService.removeUser(Long.valueOf(userId));
            System.out.println("WebSocket disconnected for userId: " + userId);
        }
    }
}