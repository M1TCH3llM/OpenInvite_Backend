package com.openinvite.backend.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class UserDTO {
    private UUID id;
    private String username;
    private String email;
    private String displayName;
    private String profilePictureUrl;
    private String bio;
    private Integer followersCount;
    private Integer followingCount;
    private Boolean isFollowing; 
    private LocalDateTime createdAt;
}