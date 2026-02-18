package com.openinvite.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventCreateRequest {
    @NotBlank
    private String title;
    
    private String description;
    
    private String location;
    
    @NotNull
    private LocalDateTime startDateTime;
    
    private LocalDateTime endDateTime;
    
    private Boolean isPublic = true;
}
