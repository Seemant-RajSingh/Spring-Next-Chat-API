package com.srs.SpringChat.controllers;

import com.srs.SpringChat.dtos.UserDTO;
import com.srs.SpringChat.models.User;
import com.srs.SpringChat.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserDTO userDto) {
        try {
            System.out.println("inside register mapping");
            userService.registerUser(userDto);
            return ResponseEntity.ok("User registered successfully!");
        } catch (Exception e) {
            System.out.println("error in registering user (from UserController - /register): " + e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error while registering user: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody UserDTO userDTO) {
        try {
            String token = userService.loginUser(userDTO.getEmail(), userDTO.getPassword());

            if (token != null) {
                return ResponseEntity.ok("Bearer token: " + token);
            } else {
                return ResponseEntity.status(401).body("Invalid credentials");
            }
        } catch (Exception e) {
            System.out.println("error in user controller /login: " + e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error while logging user in: " + e.getMessage());
        }
    }


    @GetMapping("/profile")
    public ResponseEntity<User> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        try {
            System.out.println("inside /profile");
            String token = authHeader.replace("Bearer ", "");
            System.out.println("token is: " + token);
            User currentUser = userService.getUserProfile(token);

            if (currentUser != null) {
                return ResponseEntity.ok(currentUser);  // Return user details
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);  // User not found
            }
        } catch (Exception e) {
            System.out.println("error fetching user details (from UserController - /profile): " + e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}

