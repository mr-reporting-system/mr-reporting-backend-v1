package com.mrreporting.backend.service;

import com.mrreporting.backend.dto.LoginRequest;
import com.mrreporting.backend.entity.User;
import com.mrreporting.backend.repository.UserRepository;
import com.mrreporting.backend.util.JwtUtil; // 1. Imported the new tool
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil; // 2. Injected the token printer

    public Map<String, String> loginUser(LoginRequest loginRequest) {
        Map<String, String> response = new HashMap<>();
        Optional<User> userOptional = userRepository.findByEmail(loginRequest.getEmail());

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            if (passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {

                // 3. Generate the JWT token using the user's details
                String token = jwtUtil.generateToken(user.getEmail(), user.getRole());

                // 4. Add the token to the Map so it gets sent to the frontend
                response.put("message", "Success");
                response.put("role", user.getRole());
                response.put("token", token);

                return response;
            } else {
                response.put("message", "Error: Incorrect password");
                return response;
            }
        } else {
            response.put("message", "Error: User not found with that email");
            return response;
        }
    }
}