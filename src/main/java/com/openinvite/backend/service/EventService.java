package com.openinvite.backend.service;

import com.openinvite.backend.dto.EventCreateRequest;
import com.openinvite.backend.dto.EventDTO;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {
    
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final RSVPRepository rsvpRepository;
    
    @Transactional
    public EventDTO createEvent(EventCreateRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Event event = Event.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .location(request.getLocation())
                .startDateTime(request.getStartDateTime())
                .endDateTime(request.getEndDateTime())
                .isPublic(request.getIsPublic())
                .creator(user)
                .build();
        
        event = eventRepository.save(event);
        return convertToDTO(event, user.getId());
    }
    
    public EventDTO getEventById(UUID eventId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        
        return convertToDTO(event, user.getId());
    }
    
    @Transactional
    public EventDTO updateEvent(UUID eventId, EventCreateRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        
        if (!event.getCreatedById().equals(user.getId())) {
            throw new RuntimeException("Unauthorized to update this event");
        }
        
        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setLocation(request.getLocation());
        event.setStartDateTime(request.getStartDateTime());
        event.setEndDateTime(request.getEndDateTime());
        event.setIsPublic(request.getIsPublic());
        
        event = eventRepository.save(event);
        return convertToDTO(event, user.getId());
    }
    
    @Transactional
    public void deleteEvent(UUID eventId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        
        if (!event.getCreatedById().equals(user.getId())) {
            throw new RuntimeException("Unauthorized to delete this event");
        }
        
        eventRepository.delete(event);
    }
    
    public List<EventDTO> getMyEvents() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return eventRepository.findByCreatedByIdOrderByStartDateTimeDesc(user.getId())
                .stream()
                .map(event -> convertToDTO(event, user.getId()))
                .collect(Collectors.toList());
    }
    
    public List<EventDTO> getFeed() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return eventRepository.findUpcomingEventsFromFollowedUsers(user.getId(), LocalDateTime.now())
                .stream()
                .map(event -> convertToDTO(event, user.getId()))
                .collect(Collectors.toList());
    }
    
    public List<EventDTO> getEventsInRange(LocalDateTime start, LocalDateTime end) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return eventRepository.findByStartDateTimeBetweenOrderByStartDateTime(start, end)
                .stream()
                .map(event -> convertToDTO(event, user.getId()))
                .collect(Collectors.toList());
    }
    
    private EventDTO convertToDTO(Event event, UUID currentUserId) {
        EventDTO dto = new EventDTO();
        dto.setId(event.getId());
        dto.setTitle(event.getTitle());
        dto.setDescription(event.getDescription());
        dto.setLocation(event.getLocation());
        dto.setStartDateTime(event.getStartDateTime());
        dto.setEndDateTime(event.getEndDateTime());
        dto.setIsPublic(event.getIsPublic());
        dto.setCreatedById(event.getCreatedById());
        dto.setCreatedAt(event.getCreatedAt());
        
        // Get creator info
        User creator = event.getCreator();
        if (creator != null) {
            dto.setCreatorName(creator.getDisplayName());
            dto.setCreatorUsername(creator.getUsername());
        }
        
        // Get RSVP counts
        dto.setGoingCount(rsvpRepository.countByEventIdAndStatus(event.getId(), RSVP.RSVPStatus.GOING));
        dto.setMaybeCount(rsvpRepository.countByEventIdAndStatus(event.getId(), RSVP.RSVPStatus.MAYBE));
        dto.setNotGoingCount(rsvpRepository.countByEventIdAndStatus(event.getId(), RSVP.RSVPStatus.NOT_GOING));
        
        // Get current user's RSVP
        rsvpRepository.findByUserIdAndEventId(currentUserId, event.getId())
                .ifPresent(rsvp -> dto.setCurrentUserRSVP(rsvp.getStatus().name()));
        
        return dto;
    }
}
