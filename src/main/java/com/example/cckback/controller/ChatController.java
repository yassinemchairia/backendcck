package com.example.cckback.controller;


import com.example.cckback.Entity.Utilisateur;
import com.example.cckback.Repository.UserRepository;
import com.example.cckback.dto.ChatMessageDTO;
import com.example.cckback.service.ChatService;
import com.example.cckback.service.ConnectedUsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping; import org.springframework.messaging.handler.annotation.Payload; import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController @RequestMapping("/api/chat")
public class ChatController {


    private final ChatService chatService;
    private final ConnectedUsersService connectedUsersService;
    private final UserRepository userRepository;

    @Autowired
    public ChatController(ChatService chatService, ConnectedUsersService connectedUsersService, UserRepository userRepository) {
        this.chatService = chatService;
        this.connectedUsersService = connectedUsersService;
        this.userRepository = userRepository;
    }

    @MessageMapping("/chat.send")
    public void sendMessage(@Payload ChatMessageDTO messageDTO) {
        chatService.sendMessage(messageDTO);
    }

    @GetMapping("/history/{userId1}/{userId2}")
    public List<ChatMessageDTO> getChatHistory(@PathVariable Long userId1, @PathVariable Long userId2) {
        return chatService.getChatHistory(userId1, userId2);
    }
    @GetMapping("/connected-users")
    public List<Utilisateur> getConnectedUsers() {
        return connectedUsersService.getConnectedUsers();
    }
}
