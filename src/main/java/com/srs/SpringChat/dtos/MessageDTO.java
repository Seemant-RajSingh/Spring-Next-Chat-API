package com.srs.SpringChat.dtos;

import java.time.LocalDateTime;

public class MessageDTO {

    private Long id;
    private String senderEmail;
    private String content;
    private LocalDateTime sentAt;

    public MessageDTO(Long id, String content, String senderEmail, LocalDateTime sentAt) {
        this.id = id;
        this.content = content;
        this.senderEmail = senderEmail;
        this.sentAt = sentAt;
    }

    public Long getId() {
        return id;
    }

    public MessageDTO setId(Long id) {
        this.id = id;
        return this;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }
}
