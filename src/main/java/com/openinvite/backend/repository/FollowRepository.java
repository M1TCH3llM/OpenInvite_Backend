package com.openinvite.backend.repository;

import com.openinvite.backend.model.Follow;
import com.openinvite.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {
    
    // Check if follower is following the user
    boolean existsByFollowerAndFollowing(User follower, User following);
    
    // Find follow relationship
    Optional<Follow> findByFollowerAndFollowing(User follower, User following);
    
    // Count followers
    int countByFollowing(User following);
    
    // Count following
    int countByFollower(User follower);
    
    // Get all followers of a user
    List<Follow> findByFollowing(User following);
    
    // Get all users that a user is following
    List<Follow> findByFollower(User follower);
}