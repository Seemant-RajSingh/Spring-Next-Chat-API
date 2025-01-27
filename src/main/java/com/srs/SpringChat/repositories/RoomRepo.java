package com.srs.SpringChat.repositories;

import com.srs.SpringChat.models.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoomRepo extends JpaRepository<Room, Long> {
    Optional<Room> findByRoomName(String roomName);
    Optional<Room> findByJoinUrl(String joinUrl);
}
