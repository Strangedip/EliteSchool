package com.eliteschool.auth_service.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity to store password reset tokens separately from User table
 * This provides better separation of concerns and easier token management
 */
@Entity
@Table(name = "password_reset_tokens", indexes = {
    @Index(name = "idx_token", columnList = "token"),
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_expiry", columnList = "expiry_date")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "token", nullable = false, unique = true, length = 100)
    private String token;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "used", nullable = false)
    @Builder.Default
    private Boolean used = false;

    @Column(name = "used_at")
    private LocalDateTime usedAt;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    /**
     * Check if token is expired
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiryDate);
    }

    /**
     * Check if token is valid (not used and not expired)
     */
    public boolean isValid() {
        return !this.used && !isExpired();
    }

    /**
     * Mark token as used
     */
    public void markAsUsed() {
        this.used = true;
        this.usedAt = LocalDateTime.now();
    }
}

