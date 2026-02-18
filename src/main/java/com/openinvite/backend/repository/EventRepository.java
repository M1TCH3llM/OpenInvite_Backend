package com.openinvite.backend.repository;

import com.openinvite.backend.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface EventRepository extends JpaRepository<Event, UUID> {
    
    List<Event> findByCreatedByIdOrderByStartDateTimeDesc(UUID userId);
    
    List<Event> findByStartDateTimeBetweenOrderByStartDateTime(
        LocalDateTime start, 
        LocalDateTime end
    );
    
    @Query("SELECT e FROM Event e WHERE e.creator.id IN " +
           "(SELECT f.id FROM User u JOIN u.following f WHERE u.id = :userId) " +
           "AND e.startDateTime >= :now " +
           "ORDER BY e.startDateTime ASC")
    List<Event> findUpcomingEventsFromFollowedUsers(
        @Param("userId") UUID userId, 
        @Param("now") LocalDateTime now
    );
}