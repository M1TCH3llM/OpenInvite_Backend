package com.openinvite.backend.controller;

import com.openinvite.backend.dto.RSVPRequest;
import com.openinvite.backend.dto.RSVPResponse;
import com.openinvite.backend.service.RSVPService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/events/{eventId}/rsvp")
@CrossOrigin(origins = "${cors.allowed-origins}")
@RequiredArgsConstructor
public class RSVPController {
    
    private final RSVPService rsvpService;
    
    @PostMapping
    public ResponseEntity<RSVPResponse> createOrUpdateRSVP(
            @PathVariable UUID eventId,
            @Valid @RequestBody RSVPRequest request) {
        RSVPResponse rsvp = rsvpService.createOrUpdateRSVP(eventId, request);
        return ResponseEntity.ok(rsvp);
    }
    
    @GetMapping
    public ResponseEntity<List<RSVPResponse>> getEventRSVPs(@PathVariable UUID eventId) {
        List<RSVPResponse> rsvps = rsvpService.getEventRSVPs(eventId);
        return ResponseEntity.ok(rsvps);
    }
    
    @GetMapping("/my-rsvps")
    public ResponseEntity<List<RSVPResponse>> getMyRSVPs() {
        List<RSVPResponse> rsvps = rsvpService.getMyRSVPs();
        return ResponseEntity.ok(rsvps);
    }
}