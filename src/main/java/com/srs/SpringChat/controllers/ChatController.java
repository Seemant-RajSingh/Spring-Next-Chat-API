package com.srs.SpringChat.controllers;

import com.srs.SpringChat.Utils.CustomUtils;
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
    private final CustomUtils customUtils;

    public ChatController(RoomRepo roomRepository, UserRepository userRepository, MessageRepository messageRepository, CustomUtils customUtils) {
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
        this.customUtils = customUtils;
    }

    @MessageMapping("/sendMessage/{roomName}")
    @SendTo("/topic/room/{roomName}")
    public LoadMessages sendMessage(
            @DestinationVariable String roomName,
            @RequestBody MessageRequest messageRequest) {


        Room room = customUtils.getRoomByNameOrThrow(roomName);
        User sender = customUtils.getUserByEmailOrThrow(messageRequest.getSender());    // sender's email

        // alternate
//        Optional<User> user = userRepository.findByUsername(messageRequest.getSender());
//        User sender = user.orElseThrow(() -> new RuntimeException("User not found!"));

        return customUtils.saveAndReturnMessages(messageRequest, sender, room);
    }
}
