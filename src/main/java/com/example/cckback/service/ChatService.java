// src/main/java/com/example/cckback/service/ChatService.java
package com.example.cckback.service;

import com.example.cckback.Entity.Message;
import com.example.cckback.Entity.Utilisateur;
import com.example.cckback.Repository.MessageRepository;
import com.example.cckback.Repository.UserRepository;
import com.example.cckback.dto.ChatMessageDTO;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatService {
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatService(MessageRepository messageRepository, UserRepository userRepository, SimpMessagingTemplate messagingTemplate) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.messagingTemplate = messagingTemplate;
    }

    public void sendMessage(ChatMessageDTO messageDTO) {
        System.out.println("Processing message: " + messageDTO);
        Utilisateur sender = userRepository.findById(messageDTO.getSenderId())
                .orElseThrow(() -> new RuntimeException("Sender not found: " + messageDTO.getSenderId()));
        Utilisateur receiver = userRepository.findById(messageDTO.getReceiverId())
                .orElseThrow(() -> new RuntimeException("Receiver not found: " + messageDTO.getReceiverId()));

        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(messageDTO.getContent());
        message.setFilePath(messageDTO.getFilePath());
        message.setFileType(messageDTO.getFileType());
        message.setFileName(messageDTO.getFileName());
        try {
            message.setTimestamp(LocalDateTime.parse(messageDTO.getTimestamp(), DateTimeFormatter.ISO_DATE_TIME));
        } catch (Exception e) {
            System.err.println("Error parsing timestamp: " + messageDTO.getTimestamp() + ", using current time");
            message.setTimestamp(LocalDateTime.now());
        }
        message.setStatus(Message.MessageStatus.SENT);

        messageRepository.save(message);
        System.out.println("Message saved to database: id=" + message.getId() + ", senderId=" + sender.getIdUser() + ", receiverId=" + receiver.getIdUser() + ", content=" + message.getContent() + ", filePath=" + message.getFilePath());

        // Convert message to DTO
        ChatMessageDTO responseDTO = new ChatMessageDTO(
                message.getSender().getIdUser(),
                message.getReceiver().getIdUser(),
                message.getContent(),
                message.getFilePath(),
                message.getFileType(),
                message.getFileName(),
                message.getTimestamp().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        );

        try {
            // Send to receiver
            String receiverDestination = "/topic/messages/" + receiver.getIdUser();
            System.out.println("Sending to receiver: " + receiverDestination + ", message: " + responseDTO);
            messagingTemplate.convertAndSend(receiverDestination, responseDTO);
            System.out.println("Successfully sent to receiver: " + receiverDestination);

            // Send to sender
            String senderDestination = "/topic/messages/" + sender.getIdUser();
            System.out.println("Sending to sender: " + senderDestination + ", message: " + responseDTO);
            messagingTemplate.convertAndSend(senderDestination, responseDTO);
            System.out.println("Successfully sent to sender: " + senderDestination);
        } catch (Exception e) {
            System.err.println("Error sending WebSocket message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Transactional(readOnly = true)
    public List<ChatMessageDTO> getChatHistory(Long userId1, Long userId2) {
        System.out.println("Fetching chat history for userId1=" + userId1 + ", userId2=" + userId2);
        Utilisateur user1 = userRepository.findById(userId1)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId1));
        Utilisateur user2 = userRepository.findById(userId2)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId2));

        System.out.println("User1: id=" + user1.getIdUser() + ", email=" + user1.getEmail());
        System.out.println("User2: id=" + user2.getIdUser() + ", email=" + user2.getEmail());

        List<Message> messages = messageRepository.findBySenderAndReceiverOrReceiverAndSender(user1, user2);
        System.out.println("Fetched chat history for users " + userId1 + " and " + userId2 + ": " + messages.size() + " messages");
        messages.forEach(msg -> System.out.println("Message: id=" + msg.getId() + ", senderId=" + msg.getSender().getIdUser() + ", receiverId=" + msg.getReceiver().getIdUser() + ", content=" + msg.getContent() + ", filePath=" + msg.getFilePath()));

        return messages.stream()
                .map(msg -> new ChatMessageDTO(
                        msg.getSender().getIdUser(),
                        msg.getReceiver().getIdUser(),
                        msg.getContent(),
                        msg.getFilePath(),
                        msg.getFileType(),
                        msg.getFileName(),
                        msg.getTimestamp().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                ))
                .collect(Collectors.toList());
    }
}
