package com.openinvite.backend.exception;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class ErrorResponse {
    private String message;
    private LocalDateTime timestamp;
}