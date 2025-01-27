package com.srs.SpringChat.dtos;

public class RoomDTO {
    private Long id;
    private String roomName;
    private String joinUrl;
    private String creatorEmail; // Use email, not username

    public RoomDTO(Long id, String roomName, String joinUrl, String creatorEmail) {
        this.id = id;
        this.roomName = roomName;
        this.joinUrl = joinUrl;
        this.creatorEmail = creatorEmail;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getJoinUrl() {
        return joinUrl;
    }

    public void setJoinUrl(String joinUrl) {
        this.joinUrl = joinUrl;
    }

    public String getCreatorEmail() {
        return creatorEmail;
    }

    public void setCreatorEmail(String creatorEmail) {
        this.creatorEmail = creatorEmail;
    }
}
