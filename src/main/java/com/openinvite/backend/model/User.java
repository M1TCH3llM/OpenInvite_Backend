package com.openinvite.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"followers", "following"})
@EqualsAndHashCode(exclude = {"followers", "following"})
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @NotBlank
    @Size(min = 3, max = 50)
    @Column(unique = true, nullable = false)
    private String username;
    
    @NotBlank
    @Email
    @Column(unique = true, nullable = false)
    private String email;
    
    @NotBlank
    @Column(nullable = false)
    private String password;
    
    @NotBlank
    @Size(max = 100)
    @Column(nullable = false)
    private String displayName;
    
    @Column(length = 500)
    private String profilePictureUrl;
    
    @Column(length = 500)
    private String bio;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    // Followers: Users who follow this user
    @ManyToMany
    @JoinTable(
        name = "follows",
        joinColumns = @JoinColumn(name = "following_id"),
        inverseJoinColumns = @JoinColumn(name = "follower_id")
    )
    @Builder.Default
    @JsonIgnore
    private Set<User> followers = new HashSet<>();
    
    // Following: Users this user follows
    @ManyToMany(mappedBy = "followers")
    @Builder.Default
    @JsonIgnore
    private Set<User> following = new HashSet<>();
    
    // Helper methods
    public void addFollower(User follower) {
        this.followers.add(follower);
        follower.getFollowing().add(this);
    }
    
    public void removeFollower(User follower) {
        this.followers.remove(follower);
        follower.getFollowing().remove(this);
    }
}