package com.srs.SpringChat.payload;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
public class MessageRequest {

    private String content;
    private String sender;
    private String roomName;
    private LocalDateTime messageTime;

    public String getContent() {
        return content;
    }

    public MessageRequest setContent(String content) {
        this.content = content;
        return this;
    }

    public String getSender() {
        return sender;
    }

    public MessageRequest setSender(String sender) {
        this.sender = sender;
        return this;
    }

    public String getRoomName() {
        return roomName;
    }

    public MessageRequest setRoomName(String roomName) {
        this.roomName = roomName;
        return this;
    }

    public LocalDateTime getMessageTime() {
        return messageTime;
    }

    public MessageRequest setMessageTime(LocalDateTime messageTime) {
        this.messageTime = messageTime;
        return this;
    }
}
