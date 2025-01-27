package com.srs.SpringChat.repositories;

import com.srs.SpringChat.models.Room;
import com.srs.SpringChat.models.RoomMembership;
import com.srs.SpringChat.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomMembershipRepo extends JpaRepository<RoomMembership, Long> {
    List<RoomMembership> findByUser(User user);
    Optional<RoomMembership> findByUserAndRoom(User user, Room room);
}
