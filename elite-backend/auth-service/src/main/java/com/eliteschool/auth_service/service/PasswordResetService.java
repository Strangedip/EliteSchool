package com.eliteschool.auth_service.service;

import com.eliteschool.auth_service.model.PasswordResetToken;
import com.eliteschool.auth_service.model.User;
import com.eliteschool.auth_service.repository.PasswordResetTokenRepository;
import com.eliteschool.auth_service.repository.UserRepository;
import com.eliteschool.common_utils.exception.AppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service for handling password reset operations
 * Implements secure token-based password reset with rate limiting
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.password-reset.token-expiry-minutes:30}")
    private int tokenExpiryMinutes;

    @Value("${app.password-reset.max-requests-per-hour:3}")
    private int maxRequestsPerHour;

    /**
     * Initiate password reset process
     * Generates token and sends email
     * Does not reveal if email exists (prevents user enumeration)
     */
    @Transactional
    public void initiatePasswordReset(String email) {
        User user = userRepository.findByEmail(email).orElse(null);

        // Security: Don't reveal if user exists (prevent user enumeration)
        if (user == null) {
            log.warn("Password reset requested for non-existent email: {}", email);
            return; // Return success anyway to prevent email discovery
        }

        // Rate limiting check
        if (isRateLimited(user)) {
            log.warn("Rate limit exceeded for password reset: {}", email);
            throw new AppException(
                "Too many password reset requests. Please try again after 1 hour.",
                "Too many requests",
                "RATE_LIMIT_EXCEEDED",
                HttpStatus.TOO_MANY_REQUESTS
            );
        }

        // Generate unique reset token
        String resetToken = generateResetToken();
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(tokenExpiryMinutes);

        // Create and save token entity
        PasswordResetToken tokenEntity = PasswordResetToken.builder()
            .token(resetToken)
            .user(user)
            .expiryDate(expiryTime)
            .used(false)
            .build();

        tokenRepository.save(tokenEntity);

        // Send email asynchronously
        String displayName = getUserDisplayName(user);
        emailService.sendPasswordResetEmail(email, resetToken, displayName);

        log.info("Password reset initiated for user: {} (token expires at: {})", email, expiryTime);
    }

    /**
     * Reset password using token
     * Validates token and updates user password
     */
    @Transactional
    public void resetPassword(String token, String newPassword) {
        // Find and validate token
        PasswordResetToken resetToken = tokenRepository.findValidToken(token, LocalDateTime.now())
            .orElseThrow(() -> new AppException(
                "Invalid or expired reset token. Please request a new password reset.",
                "Invalid reset token",
                "INVALID_RESET_TOKEN",
                HttpStatus.BAD_REQUEST
            ));

        // Double-check token validity (defensive programming)
        if (!resetToken.isValid()) {
            throw new AppException(
                "This reset link has already been used or has expired.",
                "Token already used or expired",
                "TOKEN_INVALID",
                HttpStatus.BAD_REQUEST
            );
        }

        User user = resetToken.getUser();

        // Update password (will be hashed by encoder)
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Mark token as used
        resetToken.markAsUsed();
        tokenRepository.save(resetToken);

        // Delete all other tokens for this user (security: invalidate all pending requests)
        tokenRepository.deleteAllByUser(user);

        // Send confirmation email
        emailService.sendPasswordResetSuccessEmail(user.getEmail(), getUserDisplayName(user));

        log.info("Password reset successful for user: {}", user.getEmail());
    }

    /**
     * Validate if a reset token is valid
     */
    @Transactional(readOnly = true)
    public boolean validateResetToken(String token) {
        return tokenRepository.findValidToken(token, LocalDateTime.now())
            .map(PasswordResetToken::isValid)
            .orElse(false);
    }

    /**
     * Generate a unique reset token
     * Uses UUID for cryptographic randomness
     */
    private String generateResetToken() {
        return UUID.randomUUID().toString();
    }

    /**
     * Check if user is rate limited
     * Prevents abuse by limiting number of reset requests per hour
     */
    private boolean isRateLimited(User user) {
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        long recentRequests = tokenRepository.countRecentRequestsByUser(user, oneHourAgo);
        
        if (recentRequests >= maxRequestsPerHour) {
            log.warn("User {} has made {} reset requests in the last hour (limit: {})", 
                user.getEmail(), recentRequests, maxRequestsPerHour);
            return true;
        }
        
        return false;
    }

    /**
     * Get display name for user (for personalized emails)
     */
    private String getUserDisplayName(User user) {
        if (user.getName() != null && !user.getName().trim().isEmpty()) {
            return user.getName().split(" ")[0];
        }
        if (user.getUsername() != null && !user.getUsername().trim().isEmpty()) {
            return user.getUsername();
        }
        return "User";
    }

    /**
     * Cleanup expired tokens (can be called by scheduled job)
     * Returns number of tokens deleted
     */
    @Transactional
    public int cleanupExpiredTokens() {
        int deleted = tokenRepository.deleteExpiredTokens(LocalDateTime.now());
        if (deleted > 0) {
            log.info("Cleaned up {} expired password reset tokens", deleted);
        }
        return deleted;
    }
}

