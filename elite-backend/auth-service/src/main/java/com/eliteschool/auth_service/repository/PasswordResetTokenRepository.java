package com.eliteschool.auth_service.repository;

import com.eliteschool.auth_service.model.PasswordResetToken;
import com.eliteschool.auth_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, UUID> {

    /**
     * Find token by token string
     */
    Optional<PasswordResetToken> findByToken(String token);

    /**
     * Find valid (not used and not expired) token
     */
    @Query("SELECT t FROM PasswordResetToken t WHERE t.token = :token AND t.used = false AND t.expiryDate > :now")
    Optional<PasswordResetToken> findValidToken(@Param("token") String token, @Param("now") LocalDateTime now);

    /**
     * Find all tokens for a user
     */
    List<PasswordResetToken> findByUser(User user);

    /**
     * Find active tokens for a user (not used and not expired)
     */
    @Query("SELECT t FROM PasswordResetToken t WHERE t.user = :user AND t.used = false AND t.expiryDate > :now")
    List<PasswordResetToken> findActiveTokensByUser(@Param("user") User user, @Param("now") LocalDateTime now);

    /**
     * Count recent reset requests by user (for rate limiting)
     */
    @Query("SELECT COUNT(t) FROM PasswordResetToken t WHERE t.user = :user AND t.createdAt > :since")
    long countRecentRequestsByUser(@Param("user") User user, @Param("since") LocalDateTime since);

    /**
     * Delete expired tokens (for cleanup job)
     */
    @Modifying
    @Query("DELETE FROM PasswordResetToken t WHERE t.expiryDate < :now")
    int deleteExpiredTokens(@Param("now") LocalDateTime now);

    /**
     * Delete all tokens for a user (when password is successfully reset)
     */
    @Modifying
    @Query("DELETE FROM PasswordResetToken t WHERE t.user = :user")
    void deleteAllByUser(@Param("user") User user);

    /**
     * Mark token as used
     */
    @Modifying
    @Query("UPDATE PasswordResetToken t SET t.used = true, t.usedAt = :usedAt WHERE t.token = :token")
    void markTokenAsUsed(@Param("token") String token, @Param("usedAt") LocalDateTime usedAt);
}

