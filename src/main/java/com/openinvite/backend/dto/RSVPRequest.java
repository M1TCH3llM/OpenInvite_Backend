package com.openinvite.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RSVPRequest {
    @NotNull
    private String status; // GOING, MAYBE, NOT_GOING
}