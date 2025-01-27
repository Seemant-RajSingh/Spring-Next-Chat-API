package com.srs.SpringChat.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "room_membership")
public class RoomMembership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(name = "joined_at", nullable = false, updatable = false)
    private LocalDateTime joinedAt = LocalDateTime.now();

    public Long getId() {
        return id;
    }

    public RoomMembership setId(Long id) {
        this.id = id;
        return this;
    }

    public User getUser() {
        return user;
    }

    public RoomMembership setUser(User user) {
        this.user = user;
        return this;
    }

    public Room getRoom() {
        return room;
    }

    public RoomMembership setRoom(Room room) {
        this.room = room;
        return this;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }

    public RoomMembership setJoinedAt(LocalDateTime joinedAt) {
        this.joinedAt = joinedAt;
        return this;
    }
}

