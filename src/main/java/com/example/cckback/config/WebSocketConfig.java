package com.example.cckback.config;



import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic"); // Active un broker simple pour les topics
        config.setApplicationDestinationPrefixes("/app"); // Préfixe pour les messages envoyés par le client
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws/notifications") // Dedicated endpoint for notifications
                .setAllowedOriginPatterns("*") // Allow all origins (adjust for production)
                .setHandshakeHandler(new DefaultHandshakeHandler())
                .withSockJS();
        registry.addEndpoint("/ws/capteurs") // Keep existing endpoint for sensors
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }
}