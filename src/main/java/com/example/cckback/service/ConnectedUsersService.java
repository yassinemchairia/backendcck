package com.example.cckback.service;

import com.example.cckback.Entity.Utilisateur;
import com.example.cckback.Repository.UserRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ConnectedUsersService {
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final Set<Long> connectedUsers = new HashSet<>();

    public ConnectedUsersService(UserRepository userRepository, SimpMessagingTemplate messagingTemplate) {
        this.userRepository = userRepository;
        this.messagingTemplate = messagingTemplate;
    }

    public void addUser(Long userId) {
        connectedUsers.add(userId);
        System.out.println("User added: " + userId + ", Total connected users: " + connectedUsers.size());
        broadcastConnectedUsers();
    }

    public void removeUser(Long userId) {
        connectedUsers.remove(userId);
        System.out.println("User removed: " + userId + ", Total connected users: " + connectedUsers.size());
        broadcastConnectedUsers();
    }

    public List<Utilisateur> getConnectedUsers() {
        return userRepository.findAllById(connectedUsers).stream()
                .collect(Collectors.toList());
    }

    private void broadcastConnectedUsers() {
        List<Utilisateur> users = getConnectedUsers();
        System.out.println("Broadcasting connected users: " + users);
        messagingTemplate.convertAndSend("/topic/connected-users", users);
    }
}