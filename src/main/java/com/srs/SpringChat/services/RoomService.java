package com.srs.SpringChat.services;

import com.srs.SpringChat.Utils.CustomUtils;
import com.srs.SpringChat.models.Message;
import com.srs.SpringChat.models.Room;
import com.srs.SpringChat.models.RoomMembership;
import com.srs.SpringChat.models.User;
import com.srs.SpringChat.payload.LoadMessages;
import com.srs.SpringChat.repositories.MessageRepository;
import com.srs.SpringChat.dtos.RoomDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoomService {

    private final MessageRepository messageRepository;
    private final CustomUtils customUtils;

    public RoomService(
            MessageRepository messageRepository,
            CustomUtils customUtils
    ) {
        this.messageRepository = messageRepository;
        this.customUtils = customUtils;
    }

    public RoomDTO createRoom(String email, String roomName) {
        customUtils.checkRoomAlreadyExists(roomName);
        User creator = customUtils.getUserByEmailOrThrow(email);
        String joinUrl = "https://chatapp.com/room/" + java.util.UUID.randomUUID();

        Room room = new Room()
                .setRoomName(roomName)
                .setCreator(creator)
                .setJoinUrl(joinUrl)
                .setCreatedAt(LocalDateTime.now());
        room = customUtils.roomRepo.save(room);

        RoomMembership roomMembership = new RoomMembership()
                .setRoom(room)
                .setUser(creator)
                .setJoinedAt(LocalDateTime.now());
        customUtils.roomMembershipRepo.save(roomMembership);

        return customUtils.mapToRoomDTO(room);
    }

    public List<RoomDTO> getRoomsForUser(String email) {
        User user = customUtils.getUserByEmailOrThrow(email);
        List<RoomMembership> memberships = customUtils.roomMembershipRepo.findByUser(user);

        return memberships.stream()
                .map(membership -> {
                    Room room = membership.getRoom();
                    String creatorEmail = room.getCreator().getEmail();
                    return new RoomDTO(room.getId(), room.getRoomName(), room.getJoinUrl(), creatorEmail);
                })
                .collect(Collectors.toList());
    }

    public List<LoadMessages> getMessagesForRoom(String roomName) {
        Room room = customUtils.getRoomByNameOrThrow(roomName);
        List<Message> messages = messageRepository.findByRoom(room);

        return messages.stream()
                .map(message -> {
                    String senderUsername = message.getSender().getUsername();
                    String senderEmail = message.getSender().getEmail();
                    return new LoadMessages(
                            message.getId(),
                            message.getContent(),
                            senderUsername,
                            senderEmail,
                            message.getSentAt().toString()
                    );
                })
                .collect(Collectors.toList());
    }

    public RoomDTO joinRoom(String email, String joinUrl) {
        User user = customUtils.getUserByEmailOrThrow(email);
        Room room = customUtils.getRoomByJoinUrlOrThrow(joinUrl);
        customUtils.checkUserAlreadyMember(user, room);

        RoomMembership membership = new RoomMembership()
                .setUser(user)
                .setRoom(room)
                .setJoinedAt(LocalDateTime.now());
        customUtils.roomMembershipRepo.save(membership);

        return customUtils.mapToRoomDTO(room);
    }
}
