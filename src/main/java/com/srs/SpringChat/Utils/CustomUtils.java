package com.srs.SpringChat.Utils;

import com.srs.SpringChat.models.Room;
import com.srs.SpringChat.models.User;
import com.srs.SpringChat.dtos.RoomDTO;
import com.srs.SpringChat.repositories.RoomMembershipRepo;
import com.srs.SpringChat.repositories.RoomRepo;
import com.srs.SpringChat.repositories.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class CustomUtils {

    public final UserRepository userRepository;
    public final RoomRepo roomRepo;
    public final RoomMembershipRepo roomMembershipRepo;

    public CustomUtils(UserRepository userRepository, RoomRepo roomRepo, RoomMembershipRepo roomMembershipRepo) {
        this.userRepository = userRepository;
        this.roomRepo = roomRepo;
        this.roomMembershipRepo = roomMembershipRepo;
    }

    public User getUserByEmailOrThrow(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    // tested
    public Room getRoomByNameOrThrow(String roomName) {
        return roomRepo.findByRoomName(roomName)
                .orElseThrow(() -> new RuntimeException("Room not found"));
    }

    // tested
    public Room getRoomByJoinUrlOrThrow(String joinUrl) {
        return roomRepo.findByJoinUrl(joinUrl)
                .orElseThrow(() -> new IllegalArgumentException("Room not found for the given URL"));
    }

    // tested
    public void checkRoomAlreadyExists(String roomName) {
        if (roomRepo.findByRoomName(roomName).isPresent()) {
            throw new IllegalArgumentException("Room with the name '" + roomName + "' already exists.");
        }
    }

    // tested
    public void checkUserAlreadyMember(User user, Room room) {
        if (roomMembershipRepo.findByUserAndRoom(user, room).isPresent()) {
            throw new IllegalStateException("User is already a member of the room");
        }
    }

    // tested
    public RoomDTO mapToRoomDTO(Room room) {
        return new RoomDTO(
                room.getId(),
                room.getRoomName(),
                room.getJoinUrl(),
                room.getCreator().getEmail()
        );
    }
}
