package com.openinvite.backend.repository;

import com.openinvite.backend.model.RSVP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RSVPRepository extends JpaRepository<RSVP, UUID> {
    
    Optional<RSVP> findByUserIdAndEventId(UUID userId, UUID eventId);
    
    List<RSVP> findByEventId(UUID eventId);
    
    List<RSVP> findByUserId(UUID userId);
    
    @Query("SELECT r FROM RSVP r WHERE r.userId = :userId AND r.status = 'GOING'")
    List<RSVP> findEventsUserIsAttending(@Param("userId") UUID userId);
    
    long countByEventIdAndStatus(UUID eventId, RSVP.RSVPStatus status);
}