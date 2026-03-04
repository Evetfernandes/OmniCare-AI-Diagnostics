package com.healthcare.telemedicine.controller;

import com.healthcare.telemedicine.model.User;
import com.healthcare.telemedicine.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

// Simplified Auth Controller for Prototype Phase
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            return ResponseEntity.badRequest().body("Error: Email is already taken!");
        }

        // Create new user's account
        user.setPassword(user.getPassword()); // Plaintext for prototype without Spring Security
        
        // Ensure role is valid
        if (user.getRole() == null) {
            user.setRole(User.Role.PATIENT);
        }

        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully!");
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody Map<String, String> loginRequest) {
        String email = loginRequest.get("email");
        String password = loginRequest.get("password");

        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (password.equals(user.getPassword())) {
                // In a full implementation, we would return a JWT here.
                // For the React prototype, we return the user details to establish session state.
                Map<String, Object> response = new HashMap<>();
                response.put("id", user.getId());
                response.put("email", user.getEmail());
                response.put("name", user.getName());
                response.put("role", user.getRole().name());
                response.put("token", "mock-jwt-token-for-prototype"); // Replace with real JWT Service later
                return ResponseEntity.ok(response);
            }
        }

        return ResponseEntity.status(401).body("Error: Unauthorized. Check credentials.");
    }
}
