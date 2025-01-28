package com.srs.SpringChat.controllers;

import com.srs.SpringChat.Utils.CustomUtils;
import com.srs.SpringChat.dtos.RoomDTO;
import com.srs.SpringChat.models.Room;
import com.srs.SpringChat.models.User;
import com.srs.SpringChat.payload.LoadMessages;
import com.srs.SpringChat.services.RoomService;
import com.srs.SpringChat.services.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/rooms")
public class RoomController {


    private final RoomService roomService;
    private final JwtService jwtService;
    private final CustomUtils customUtils;

    public RoomController(
            RoomService roomService,
            JwtService jwtService,
            CustomUtils customUtils
    ) {
        this.roomService = roomService;
        this.jwtService = jwtService;
        this.customUtils = customUtils;
    }



    @PostMapping("/create")
    public ResponseEntity<?> createRoom(@RequestHeader("Authorization") String token, @RequestParam String roomName) {
        try {
            String email = jwtService.extractEmailFromToken(token.substring(7));
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
            String email = jwtService.extractEmailFromToken(token.substring(7));
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
            String userEmailFromToken = jwtService.extractEmailFromToken(token);

            // tested
            if (!email.equals(userEmailFromToken)) {
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
            String token = authHeader.replace("Bearer ", "");
            String userEmailFromToken = jwtService.extractEmailFromToken(token);

            User user = customUtils.getUserByEmailOrThrow(userEmailFromToken);
            Room room = customUtils.getRoomByNameOrThrow(roomName);

            // validate user is from the room for which messages are requested
            try {
                customUtils.membershipOptional(user, room);
            } catch (RuntimeException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(e.getMessage());
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
