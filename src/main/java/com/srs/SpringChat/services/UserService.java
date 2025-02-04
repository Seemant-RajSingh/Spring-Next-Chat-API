package com.srs.SpringChat.services;

import com.srs.SpringChat.dtos.UserDTO;
import com.srs.SpringChat.exceptions.UserAlreadyExistsException;
import com.srs.SpringChat.models.Role;
import com.srs.SpringChat.models.User;
import com.srs.SpringChat.repositories.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public UserService(
            UserRepository userRepository,
            BCryptPasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthenticationManager authenticationManager
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    // --------------------------------- REGISTER USER -----------------------------------
    private static final String EMAIL_EXISTS_ERROR = "Email already exists!";
    private static final String INVALID_EMAIL_ERROR = "Invalid email format!";
    private static final String WEAK_PASSWORD_ERROR = "Password must be at least 8 characters, include a number and a special character.";
    private static final String INVALID_USERNAME_ERROR = "Username can only contain alphabets, numbers and spaces.";

    // Email validation requirements
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$"
    );
    // Password validation requirements
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[@#$%^&+=!]).{8,}$"
    );
    // Username validation requirements
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9 ]+$");


    public void registerUser(final UserDTO userDto) {
        System.out.println("Inside registerUser service");

        // Sanitizing inputs
        validateInputsAtRegister(userDto);

        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new UserAlreadyExistsException(EMAIL_EXISTS_ERROR);
        }


        User user = new User();
        user.setUsername(userDto.getUsername().trim());
        user.setEmail(userDto.getEmail().trim().toLowerCase());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setRole(Role.USER);

        userRepository.save(user);
    }

    private void validateInputsAtRegister(final UserDTO userDto) {
        // null input validation
        if (userDto == null) throw new IllegalArgumentException("User data cannot be null.");
        if (!StringUtils.hasText(userDto.getUsername())) throw new IllegalArgumentException("Username cannot be empty.");
        if (!StringUtils.hasText(userDto.getEmail())) throw new IllegalArgumentException("User email cannot be empty.");
        if (!StringUtils.hasText(userDto.getPassword())) throw new IllegalArgumentException("User password cannot be empty.");

        // pattern matches
        if (!USERNAME_PATTERN.matcher(userDto.getUsername()).matches()) {
            throw new IllegalArgumentException(INVALID_USERNAME_ERROR);
        }
        if (!EMAIL_PATTERN.matcher(userDto.getEmail()).matches()) {
            throw new IllegalArgumentException(INVALID_EMAIL_ERROR);
        }
        if (!PASSWORD_PATTERN.matcher(userDto.getPassword()).matches()) {
            throw new IllegalArgumentException(WEAK_PASSWORD_ERROR);
        }
    }
    // -----------------------------------------------------------------------
    // ---------------------------- LOGIN ------------------------------------
    public String loginUser(String email, String password) {
        // sanitizing inputs
        validateInputsAtLogin(email, password);

        // authenticating user - check
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        return jwtService.generateToken(email);
    }

    private void validateInputsAtLogin(String email, String password) {
        if (email == null) throw new IllegalArgumentException("Email cannot be null.");
        if (password == null) throw new IllegalArgumentException("Password cannot be null");

        email = email.trim().toLowerCase();

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException(INVALID_EMAIL_ERROR);
        }
    }


    // -----------------------------------------------------------------------
    // ---------------------------- FETCH USER PROFILE ------------------------------------
    public User getUserProfile(String token) {
        String email = jwtService.extractEmailFromToken(token);
        Optional<User> userOptional = userRepository.findByEmail(email);
        // map to DTO and return
        return userOptional.orElse(null);
    }

}
