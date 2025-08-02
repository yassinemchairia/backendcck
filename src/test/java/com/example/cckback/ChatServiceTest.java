package com.example.cckback;

import com.example.cckback.Entity.*;
import com.example.cckback.Repository.MessageRepository;
import com.example.cckback.Repository.UserRepository;
import com.example.cckback.dto.ChatMessageDTO;
import com.example.cckback.service.ChatService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@Nested
@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private ChatService chatService;

    private Utilisateur sender;
    private Utilisateur receiver;
    private ChatMessageDTO messageDTO;

    @BeforeEach
    void setUp() {
        sender = new Technicien();
        sender.setIdUser(1L);
        sender.setEmail("sender@example.com");

        receiver = new Administrateur();
        receiver.setIdUser(2L);
        receiver.setEmail("receiver@example.com");

        messageDTO = new ChatMessageDTO(
                1L, 2L, "Hello",
                "/path/to/file", "image/png", "file.png",
                LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
        );
    }

    @Test
    void sendMessage_ShouldSaveAndBroadcastMessage() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(userRepository.findById(2L)).thenReturn(Optional.of(receiver));
        when(messageRepository.save(any(Message.class))).thenAnswer(inv -> {
            Message msg = inv.getArgument(0);
            msg.setId(1L);
            return msg;
        });

        chatService.sendMessage(messageDTO);

        verify(messageRepository).save(any(Message.class));
        verify(messagingTemplate, times(2)).convertAndSend(anyString(), any(ChatMessageDTO.class));
    }

    @Test
    void getChatHistory_ShouldReturnMessagesBetweenUsers() {
        Message message = new Message();
        message.setId(1L);
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent("Hello");
        message.setTimestamp(LocalDateTime.now());

        when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(userRepository.findById(2L)).thenReturn(Optional.of(receiver));
        when(messageRepository.findBySenderAndReceiverOrReceiverAndSender(sender, receiver))
                .thenReturn(List.of(message));

        List<ChatMessageDTO> result = chatService.getChatHistory(1L, 2L);

        assertEquals(1, result.size());
        assertEquals("Hello", result.get(0).getContent());
    }
}
