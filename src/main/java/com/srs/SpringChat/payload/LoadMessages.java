package com.srs.SpringChat.payload;

public class LoadMessages {
    private long id;
    private String content;
    private String senderUsername;
    private String senderEmail;
    private String sentAt;

    // Constructor
    public LoadMessages(long id, String content, String senderUsername, String senderEmail, String sentAt) {
        this.id = id;
        this.content = content;
        this.senderUsername = senderUsername;
        this.senderEmail = senderEmail;
        this.sentAt = sentAt;
    }

    // Getters and Setters (or use Lombok annotations for brevity)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSenderUsername() {
        return senderUsername;
    }

    public void setSenderUsername(String senderUsername) {
        this.senderUsername = senderUsername;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public String getSentAt() {
        return sentAt;
    }

    public void setSentAt(String sentAt) {
        this.sentAt = sentAt;
    }
}

