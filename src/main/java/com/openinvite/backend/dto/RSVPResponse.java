package com.openinvite.backend.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class RSVPResponse {
    private UUID id;
    private UUID userId;
    private String username;
    private String displayName;
    private UUID eventId;
    private String status;
    private LocalDateTime createdAt;
}