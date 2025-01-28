package com.srs.SpringChat.controllers;

import com.srs.SpringChat.dtos.MessageDTO;
import com.srs.SpringChat.models.Message;
import com.srs.SpringChat.models.Room;
import com.srs.SpringChat.models.User;
import com.srs.SpringChat.payload.MessageRequest;
import com.srs.SpringChat.payload.LoadMessages;
import com.srs.SpringChat.repositories.MessageRepository;
import com.srs.SpringChat.repositories.RoomRepo;
import com.srs.SpringChat.repositories.UserRepository;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.Optional;

@Controller
@CrossOrigin("http://localhost:3000")
public class ChatController {

    private final RoomRepo roomRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    public ChatController(RoomRepo roomRepository, UserRepository userRepository, MessageRepository messageRepository) {
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
    }

    @MessageMapping("/sendMessage/{roomName}")
    @SendTo("/topic/room/{roomName}")
    public LoadMessages sendMessage(
            @DestinationVariable String roomName,
            @RequestBody MessageRequest messageRequest) {

        Room room = roomRepository.findByRoomName(roomName).orElseThrow(() -> new RuntimeException("Room not found!"));

        Optional<User> user = userRepository.findByUsername(messageRequest.getSender());
        User sender = user.orElseThrow(() -> new RuntimeException("User not found!"));

        Message message = new Message();
        message.setContent(messageRequest.getContent());
        message.setSender(sender);
        message.setRoom(room);
        message.setSentAt(LocalDateTime.now());

        System.out.println("message created: " + message);

        messageRepository.save(message);
        System.out.println("message sent");

        // Return the message using LoadMessages
        LoadMessages loadMessage = new LoadMessages(
                message.getId(),
                message.getContent(),
                sender.getUsername(),
                sender.getEmail(),
                message.getSentAt().toString()
        );

        return loadMessage;
    }
}
