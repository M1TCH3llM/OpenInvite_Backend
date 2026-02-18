package com.openinvite.backend.controller;

import com.openinvite.backend.dto.EventCreateRequest;
import com.openinvite.backend.dto.EventDTO;
import com.openinvite.backend.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = "${cors.allowed-origins}")
@RequiredArgsConstructor
public class EventController {
    
    private final EventService eventService;
    
    @PostMapping
    public ResponseEntity<EventDTO> createEvent(@Valid @RequestBody EventCreateRequest request) {
        EventDTO event = eventService.createEvent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(event);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<EventDTO> getEvent(@PathVariable UUID id) {
        EventDTO event = eventService.getEventById(id);
        return ResponseEntity.ok(event);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<EventDTO> updateEvent(
            @PathVariable UUID id,
            @Valid @RequestBody EventCreateRequest request) {
        EventDTO event = eventService.updateEvent(id, request);
        return ResponseEntity.ok(event);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEvent(@PathVariable UUID id) {
        eventService.deleteEvent(id);
        return ResponseEntity.ok("Event deleted successfully");
    }
    
    @GetMapping("/my-events")
    public ResponseEntity<List<EventDTO>> getMyEvents() {
        List<EventDTO> events = eventService.getMyEvents();
        return ResponseEntity.ok(events);
    }
    
    @GetMapping("/feed")
    public ResponseEntity<List<EventDTO>> getFeed() {
        List<EventDTO> events = eventService.getFeed();
        return ResponseEntity.ok(events);
    }
    
    @GetMapping("/calendar")
    public ResponseEntity<List<EventDTO>> getEventsInRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        List<EventDTO> events = eventService.getEventsInRange(start, end);
        return ResponseEntity.ok(events);
    }
}