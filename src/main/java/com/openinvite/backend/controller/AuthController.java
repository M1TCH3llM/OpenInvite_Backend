package com.openinvite.backend.controller;

import com.openinvite.backend.dto.AuthResponse;
import com.openinvite.backend.dto.LoginRequest;
import com.openinvite.backend.dto.RegisterRequest;
import com.openinvite.backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "${cors.allowed-origins}")
public class AuthController {
    
    private final AuthService authService;
    
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        // With JWT, logout is typically handled client-side by removing the token
        return ResponseEntity.ok("Logged out successfully");
    }
}