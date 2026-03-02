package com.mrreporting.backend.service;

import com.mrreporting.backend.dto.LoginRequest;
import com.mrreporting.backend.entity.User;
import com.mrreporting.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public String loginUser(LoginRequest loginRequest) {
        Optional<User> userOptional = userRepository.findByEmail(loginRequest.getEmail());

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            
            // Use .matches(rawPassword, hashedPassword)
            if (passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                return "Success! Redirect to: " + user.getRole() + " dashboard";
            } else {
                return "Error: Incorrect password";
            }
        } else {
            return "Error: User not found with that email";
        }
    }
}