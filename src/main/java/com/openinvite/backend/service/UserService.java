package com.openinvite.backend.service;

import com.openinvite.backend.dto.UserDTO;
import com.openinvite.backend.model.User;
import com.openinvite.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    
    public UserDTO getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return convertToDTO(user);
    }
    
    public UserDTO getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return convertToDTO(user);
    }
    
    public List<UserDTO> searchUsers(String query) {
        return userRepository.searchUsers(query).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public UserDTO updateProfile(UUID userId, UserDTO updateRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Verify current user is updating their own profile
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!user.getUsername().equals(currentUsername)) {
            throw new RuntimeException("Unauthorized");
        }
        
        // Update fields
        if (updateRequest.getDisplayName() != null) {
            user.setDisplayName(updateRequest.getDisplayName());
        }
        if (updateRequest.getBio() != null) {
            user.setBio(updateRequest.getBio());
        }
        if (updateRequest.getProfilePictureUrl() != null) {
            user.setProfilePictureUrl(updateRequest.getProfilePictureUrl());
        }
        
        user = userRepository.save(user);
        return convertToDTO(user);
    }
    
    public List<UserDTO> getFollowers(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getFollowers().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<UserDTO> getFollowing(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getFollowing().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public void followUser(UUID userIdToFollow) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("Current user not found"));
        
        User userToFollow = userRepository.findById(userIdToFollow)
                .orElseThrow(() -> new RuntimeException("User to follow not found"));
        
        if (currentUser.getId().equals(userIdToFollow)) {
            throw new RuntimeException("Cannot follow yourself");
        }
        
        userToFollow.addFollower(currentUser);
        userRepository.save(userToFollow);
    }
    
    @Transactional
    public void unfollowUser(UUID userIdToUnfollow) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("Current user not found"));
        
        User userToUnfollow = userRepository.findById(userIdToUnfollow)
                .orElseThrow(() -> new RuntimeException("User to unfollow not found"));
        
        userToUnfollow.removeFollower(currentUser);
        userRepository.save(userToUnfollow);
    }
    
    private UserDTO convertToDTO(User user) {
        UserDTO dto = modelMapper.map(user, UserDTO.class);
        dto.setFollowersCount(user.getFollowers().size());
        dto.setFollowingCount(user.getFollowing().size());
        return dto;
    }
}