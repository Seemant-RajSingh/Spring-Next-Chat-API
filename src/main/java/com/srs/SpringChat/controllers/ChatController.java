package com.srs.SpringChat.controllers;

import com.srs.SpringChat.Utils.CustomUtils;

import com.srs.SpringChat.models.Room;
import com.srs.SpringChat.models.User;
import com.srs.SpringChat.payload.MessageRequest;
import com.srs.SpringChat.payload.LoadMessages;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;


@Controller
@CrossOrigin("http://localhost:3000")
public class ChatController {


    private final CustomUtils customUtils;

    public ChatController(CustomUtils customUtils) {
        this.customUtils = customUtils;
    }

    @MessageMapping("/sendMessage/{roomName}")
    @SendTo("/topic/room/{roomName}")
    public LoadMessages sendMessage(
            @DestinationVariable String roomName,
            @RequestBody MessageRequest messageRequest) {

        System.out.println("in sendMessage/{roomName}");

        Room room = customUtils.getRoomByNameOrThrow(roomName);
        User sender = customUtils.getUserByEmailOrThrow(messageRequest.getSender());    // sender's email

        // alternate
//        Optional<User> user = userRepository.findByUsername(messageRequest.getSender());
//        User sender = user.orElseThrow(() -> new RuntimeException("User not found!"));

        return customUtils.saveAndReturnMessages(messageRequest, sender, room);
    }
}
