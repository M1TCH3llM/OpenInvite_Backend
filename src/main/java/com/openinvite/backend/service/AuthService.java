package com.openinvite.backend.service;

import com.openinvite.backend.dto.AuthResponse;
import com.openinvite.backend.dto.LoginRequest;
import com.openinvite.backend.dto.RegisterRequest;
import com.openinvite.backend.dto.UserDTO;
import com.openinvite.backend.model.User;
import com.openinvite.backend.repository.UserRepository;
import com.openinvite.backend.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;
    private final ModelMapper modelMapper;
    
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Check if username exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        
        // Check if email exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        
        // Create user
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .displayName(request.getDisplayName())
                .build();
        
        user = userRepository.save(user);
        
        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = tokenProvider.generateToken(authentication);
        
        UserDTO userDTO = convertToDTO(user);
        
        return AuthResponse.builder()
                .token(token)
                .user(userDTO)
                .build();
    }
    
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = tokenProvider.generateToken(authentication);
        
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        UserDTO userDTO = convertToDTO(user);
        
        return AuthResponse.builder()
                .token(token)
                .user(userDTO)
                .build();
    }
    
    private UserDTO convertToDTO(User user) {
        UserDTO dto = modelMapper.map(user, UserDTO.class);
        dto.setFollowersCount(user.getFollowers().size());
        dto.setFollowingCount(user.getFollowing().size());
        return dto;
    }
}