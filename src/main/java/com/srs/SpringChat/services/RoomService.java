package com.srs.SpringChat.services;

import com.srs.SpringChat.dtos.MessageDTO;
import com.srs.SpringChat.models.Message;
import com.srs.SpringChat.models.Room;
import com.srs.SpringChat.models.RoomMembership;
import com.srs.SpringChat.models.User;
import com.srs.SpringChat.payload.LoadMessages;
import com.srs.SpringChat.repositories.MessageRepository;
import com.srs.SpringChat.repositories.RoomRepo;
import com.srs.SpringChat.repositories.RoomMembershipRepo;
import com.srs.SpringChat.repositories.UserRepository;
import com.srs.SpringChat.dtos.RoomDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RoomService {

    @Autowired
    private RoomRepo roomRepo;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoomMembershipRepo roomMembershipRepo;

    @Autowired
    private MessageRepository messageRepository;

    public RoomDTO createRoom(String email, String roomName) {

        Optional<Room> existingRoom = roomRepo.findByRoomName(roomName);
        if (existingRoom.isPresent()) {
            throw new IllegalArgumentException("Room with the name '" + roomName + "' already exists.");
        }

        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        User creator = userOptional.get();

        String joinUrl = "https://chatapp.com/room/" + UUID.randomUUID().toString();

        Room room = new Room()
                .setRoomName(roomName)
                .setCreator(creator)
                .setJoinUrl(joinUrl)
                .setCreatedAt(LocalDateTime.now());

        room = roomRepo.save(room);

        RoomMembership roomMembership = new RoomMembership()
                .setRoom(room)
                .setUser(creator)
                .setJoinedAt(LocalDateTime.now());

        roomMembershipRepo.save(roomMembership);

        return mapToRoomDTO(room);
    }

    // ---------------------- FETCH ROOMS FOR USER ----------------------
    public List<RoomDTO> getRoomsForUser(String email) {
        // Fetch user by email
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }
        User user = userOptional.get();

        // Get all room memberships for the user
        List<RoomMembership> memberships = roomMembershipRepo.findByUser(user);

        // Convert RoomMemberships to RoomDTOs, including the creator's email
        return memberships.stream()
                .map(membership -> {
                    Room room = membership.getRoom();
                    String creatorEmail = room.getCreator().getEmail();  // Get the creator's email
                    return new RoomDTO(room.getId(), room.getRoomName(), room.getJoinUrl(), creatorEmail);
                })
                .collect(Collectors.toList());
    }

    // ---------------------- LOAD MESSAGES FOR CONNECTED USER --------------------
    public List<LoadMessages> getMessagesForRoom(String roomName) {
        // Fetch room by roomName
        Optional<Room> room = roomRepo.findByRoomName(roomName);
        if (room.isEmpty()) {
            throw new RuntimeException("Room not found");
        }

        // Fetch all messages in the room
        List<Message> messages = messageRepository.findByRoom(room.get());

        // Convert messages to LoadMessages and return the required fields
        return messages.stream()
                .map(message -> {
                    // Get sender's username and email
                    String senderUsername = message.getSender().getUsername();
                    String senderEmail = message.getSender().getEmail();
                    // Create LoadMessages instance for each message
                    return new LoadMessages(
                            message.getId(),
                            message.getContent(),
                            senderUsername,  // Sender's username
                            senderEmail,     // Sender's email
                            message.getSentAt().toString()  // Assuming sentAt is a LocalDateTime
                    );
                })
                .collect(Collectors.toList());
    }




    // ------------------- HANDLE JOIN -------------------------------
    public RoomDTO joinRoom(String email, String joinUrl) {
        // Fetch the user by email
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }
        User user = userOptional.get();

        // Find the room by joinUrl
        Optional<Room> roomOptional = roomRepo.findByJoinUrl(joinUrl);
        if (roomOptional.isEmpty()) {
            throw new IllegalArgumentException("Room not found for the given URL");
        }
        Room room = roomOptional.get();

        // Checking if user already exists in the room
        Optional<RoomMembership> membershipOptional = roomMembershipRepo.findByUserAndRoom(user, room);
        if (membershipOptional.isPresent()) {
            throw new IllegalStateException("User is already a member of the room");
        }

        // Create a new membership for the user
        RoomMembership membership = new RoomMembership()
                .setUser(user)
                .setRoom(room)
                .setJoinedAt(LocalDateTime.now());
        roomMembershipRepo.save(membership);

        // Return the RoomDTO for the room that the user just joined
        return mapToRoomDTO(room);
    }

    // ------------------- COMMON METHODS -----------------------------
    private RoomDTO mapToRoomDTO(Room room) {
        return new RoomDTO(
                room.getId(),
                room.getRoomName(),
                room.getJoinUrl(),
                room.getCreator().getEmail()
        );
    }
}
