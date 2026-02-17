package com.openinvite.backend.controller;

import com.openinvite.backend.dto.UserDTO;
import com.openinvite.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "${cors.allowed-origins}")
public class UserController {
    
    private final UserService userService;
    
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser() {
        UserDTO user = userService.getCurrentUser();
        return ResponseEntity.ok(user);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable UUID id) {
        UserDTO user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }
    
    @PutMapping("/me")
    public ResponseEntity<UserDTO> updateProfile(@RequestBody UserDTO updateRequest) {
        UserDTO currentUser = userService.getCurrentUser();
        UserDTO updatedUser = userService.updateProfile(currentUser.getId(), updateRequest);
        return ResponseEntity.ok(updatedUser);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<UserDTO>> searchUsers(@RequestParam String q) {
        List<UserDTO> users = userService.searchUsers(q);
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/{id}/followers")
    public ResponseEntity<List<UserDTO>> getFollowers(@PathVariable UUID id) {
        List<UserDTO> followers = userService.getFollowers(id);
        return ResponseEntity.ok(followers);
    }
    
    @GetMapping("/{id}/following")
    public ResponseEntity<List<UserDTO>> getFollowing(@PathVariable UUID id) {
        List<UserDTO> following = userService.getFollowing(id);
        return ResponseEntity.ok(following);
    }
    
    @PostMapping("/{id}/follow")
    public ResponseEntity<String> followUser(@PathVariable UUID id) {
        userService.followUser(id);
        return ResponseEntity.ok("User followed successfully");
    }
    
    @DeleteMapping("/{id}/unfollow")
    public ResponseEntity<String> unfollowUser(@PathVariable UUID id) {
        userService.unfollowUser(id);
        return ResponseEntity.ok("User unfollowed successfully");
    }
}