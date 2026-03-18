package com.mrreporting.backend.controller;

import com.mrreporting.backend.dto.LoginRequest;
import com.mrreporting.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest loginRequest) {
        
        Map<String, String> responseData = userService.loginUser(loginRequest);
        
        // Check if the message says "Success"
        if ("Success".equals(responseData.get("message"))) {
            return ResponseEntity.ok(responseData);
        } else {
            return ResponseEntity.badRequest().body(responseData);
        }
    }
}