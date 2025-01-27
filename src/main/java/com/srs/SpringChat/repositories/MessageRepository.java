package com.srs.SpringChat.repositories;

import com.srs.SpringChat.models.Message;
import com.srs.SpringChat.models.Room;
import com.srs.SpringChat.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByRoom(Room room);
}
