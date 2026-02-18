package com.openinvite.backend.service;

import com.openinvite.backend.dto.RSVPRequest;
import com.openinvite.backend.dto.RSVPResponse;
import com.openinvite.backend.model.Event;
import com.openinvite.backend.model.RSVP;
import com.openinvite.backend.model.User;
import com.openinvite.backend.repository.EventRepository;
import com.openinvite.backend.repository.RSVPRepository;
import com.openinvite.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RSVPService {
    
    private final RSVPRepository rsvpRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    
    @Transactional
    public RSVPResponse createOrUpdateRSVP(UUID eventId, RSVPRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        
        RSVP.RSVPStatus status;
        try {
            status = RSVP.RSVPStatus.valueOf(request.getStatus().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid RSVP status: " + request.getStatus());
        }
        
        RSVP rsvp = rsvpRepository.findByUserIdAndEventId(user.getId(), eventId)
                .orElse(RSVP.builder()
                        .user(user)
                        .event(event)
                        .build());
        
        rsvp.setStatus(status);
        rsvp = rsvpRepository.save(rsvp);
        
        return convertToResponse(rsvp);
    }
    
    public List<RSVPResponse> getEventRSVPs(UUID eventId) {
        return rsvpRepository.findByEventId(eventId)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public List<RSVPResponse> getMyRSVPs() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return rsvpRepository.findByUserId(user.getId())
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    private RSVPResponse convertToResponse(RSVP rsvp) {
        RSVPResponse response = new RSVPResponse();
        response.setId(rsvp.getId());
        response.setUserId(rsvp.getUserId());
        response.setEventId(rsvp.getEventId());
        response.setStatus(rsvp.getStatus().name());
        response.setCreatedAt(rsvp.getCreatedAt());
        
        User user = rsvp.getUser();
        if (user != null) {
            response.setUsername(user.getUsername());
            response.setDisplayName(user.getDisplayName());
        }
        
        return response;
    }
}