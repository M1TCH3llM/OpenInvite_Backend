package com.openinvite.backend.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class EventDTO {
    private UUID id;
    private String title;
    private String description;
    private String location;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private Boolean isPublic;
    private UUID createdById;
    private String creatorName;
    private String creatorUsername;
    private Long goingCount;
    private Long maybeCount;
    private Long notGoingCount;
    private String currentUserRSVP; // GOING, MAYBE, NOT_GOING, or null
    private LocalDateTime createdAt;
}