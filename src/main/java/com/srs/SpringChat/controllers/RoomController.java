package com.srs.SpringChat.controllers;

import com.srs.SpringChat.dtos.RoomDTO;
import com.srs.SpringChat.models.Message;
import com.srs.SpringChat.models.Room;
import com.srs.SpringChat.dtos.MessageDTO;
import com.srs.SpringChat.models.RoomMembership;
import com.srs.SpringChat.models.User;
import com.srs.SpringChat.payload.LoadMessages;
import com.srs.SpringChat.repositories.MessageRepository;
import com.srs.SpringChat.repositories.RoomMembershipRepo;
import com.srs.SpringChat.repositories.RoomRepo;
import com.srs.SpringChat.repositories.UserRepository;
import com.srs.SpringChat.services.RoomService;
import com.srs.SpringChat.services.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final UserRepository userRepository;
    private final RoomRepo roomRepository;
    private final RoomMembershipRepo roomMembershipRepo;
    private final RoomService roomService;
    private final JwtService jwtService;

    public RoomController(
            UserRepository userRepository,
            RoomRepo roomRepository,
            RoomMembershipRepo roomMembershipRepo,
            RoomService roomService,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.roomRepository = roomRepository;
        this.roomMembershipRepo = roomMembershipRepo;
        this.roomService = roomService;
        this.jwtService = jwtService;
    }



    @PostMapping("/create")
    public ResponseEntity<?> createRoom(@RequestHeader("Authorization") String token, @RequestParam String roomName) {
        try {
            String email = jwtService.extractUserName(token.substring(7));
            RoomDTO createdRoom = roomService.createRoom(email, roomName);

            return ResponseEntity.status(HttpStatus.CREATED).body(createdRoom);

        } catch (Exception e) {
            System.err.println("Error in RoomController /create: " + e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Failed to create room: " + e.getMessage());
        }
    }


    @PostMapping("/join")
    public ResponseEntity<?> joinRoom(@RequestHeader("Authorization") String token, @RequestBody String joinUrl) {
        try {
            String email = jwtService.extractUserName(token.substring(7));
            return ResponseEntity.ok(roomService.joinRoom(email, joinUrl));
        } catch (Exception e) {
            System.err.println("Error in RoomController /join: " + e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Failed to join room: " + e.getMessage());
        }
    }



    @GetMapping("/user/{email}/getRooms")
    public ResponseEntity<?> getRoomsForUser(@PathVariable String email, @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            String usernameFromToken = jwtService.extractUserName(token);

            if (!email.equals(usernameFromToken)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Bad request - requesting rooms for a different user");
            }

            return ResponseEntity.ok(roomService.getRoomsForUser(email));
        } catch (Exception e) {
            System.err.println("Error in RoomController /getRooms: " + e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to fetch rooms: " + e.getMessage());
        }
    }



    @GetMapping("/{roomName}/messages")
    public ResponseEntity<?> getMessages(@PathVariable String roomName, @RequestHeader("Authorization") String authHeader) {
        try {
            // Extract the token and username from it
            String token = authHeader.replace("Bearer ", "");
            String usernameFromToken = jwtService.extractUserName(token);

            // Check if the user exists in the repository by username
            Optional<User> userOptional = userRepository.findByEmail(usernameFromToken);  // Changed to find by username
            if (userOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Bad request - user does not exist");
            }
            User user = userOptional.get();

            // Fetch the room by its name
            Optional<Room> roomOptional = roomRepository.findByRoomName(roomName);
            if (roomOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Room not found");
            }
            Room room = roomOptional.get();

            // Check if the user is a member of the room
            Optional<RoomMembership> membershipOptional = roomMembershipRepo.findByUserAndRoom(user, room);
            if (membershipOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Bad request - user is not a member of the room");
            }

            // Fetch messages for the room and convert to LoadMessages payload
            List<LoadMessages> loadMessages = roomService.getMessagesForRoom(roomName);
            return ResponseEntity.ok(loadMessages);

        } catch (Exception e) {
            System.err.println("Error in MessageController /getMessages: " + e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to fetch messages: " + e.getMessage());
        }
    }

}
