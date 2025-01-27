package com.srs.SpringChat.models;

import jakarta.persistence.*; // not javax
import java.time.LocalDateTime;

@Entity
@Table(name = "room")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100, unique=true)
    private String roomName;

    @ManyToOne
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    @Column(nullable = false, unique = true, length = 255)
    private String joinUrl;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public Room setId(Long id) {
        this.id = id;
        return this;
    }

    public String getRoomName() {
        return roomName;
    }

    public Room setRoomName(String roomName) {
        this.roomName = roomName;
        return this;
    }

    public User getCreator() {
        return creator;
    }

    public Room setCreator(User creator) {
        this.creator = creator;
        return this;
    }

    public String getJoinUrl() {
        return joinUrl;
    }

    public Room setJoinUrl(String joinUrl) {
        this.joinUrl = joinUrl;
        return this;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Room setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }
}

