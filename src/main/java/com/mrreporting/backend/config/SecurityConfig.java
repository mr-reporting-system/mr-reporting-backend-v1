package com.mrreporting.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 1. Disable CSRF because we are building a stateless REST API
            .csrf(csrf -> csrf.disable())
            
            // 2. Configure which URLs are public and which are private
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/users/login", "/api/contacts/submit").permitAll() // Public Door
                .anyRequest().authenticated()                   // Everything else is locked
            );

        return http.build();
    }
}