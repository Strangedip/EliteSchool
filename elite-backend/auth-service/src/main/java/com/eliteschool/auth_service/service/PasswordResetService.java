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

    @Transactional
    public void initiatePasswordReset(String email) {
        User user = userRepository.findByEmail(email).orElse(null);

        // Don't reveal whether the email exists (prevents enumeration)
        if (user == null) {
            log.warn("Password reset requested for non-existent email: {}", email);
            return;
        }

        if (isRateLimited(user)) {
            log.warn("Rate limit exceeded for password reset: {}", email);
            throw new AppException(
                "Too many password reset requests. Please try again after 1 hour.",
                "Too many requests",
                "RATE_LIMIT_EXCEEDED",
                HttpStatus.TOO_MANY_REQUESTS
            );
        }

        String resetToken = generateResetToken();
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(tokenExpiryMinutes);

        PasswordResetToken tokenEntity = PasswordResetToken.builder()
            .token(resetToken)
            .user(user)
            .expiryDate(expiryTime)
            .used(false)
            .build();

        tokenRepository.save(tokenEntity);

        String displayName = getUserDisplayName(user);
        try {
            emailService.sendPasswordResetEmail(email, resetToken, displayName);
        } catch (Exception ex) {
            // Token is saved; do not fail the API when SMTP is misconfigured (common in demos)
            log.error("Password reset email could not be sent to {} — check EMAIL_* settings: {}",
                    email, ex.getMessage());
        }

        log.info("Password reset initiated for user: {} (token expires at: {})", email, expiryTime);
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findValidToken(token, LocalDateTime.now())
            .orElseThrow(() -> new AppException(
                "Invalid or expired reset token. Please request a new password reset.",
                "Invalid reset token",
                "INVALID_RESET_TOKEN",
                HttpStatus.BAD_REQUEST
            ));

        // Defensive re-check after query
        if (!resetToken.isValid()) {
            throw new AppException(
                "This reset link has already been used or has expired.",
                "Token already used or expired",
                "TOKEN_INVALID",
                HttpStatus.BAD_REQUEST
            );
        }

        User user = resetToken.getUser();

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        resetToken.markAsUsed();
        tokenRepository.save(resetToken);

        // Invalidate all pending reset tokens for this user
        tokenRepository.deleteAllByUser(user);

        emailService.sendPasswordResetSuccessEmail(user.getEmail(), getUserDisplayName(user));

        log.info("Password reset successful for user: {}", user.getEmail());
    }

    @Transactional(readOnly = true)
    public boolean validateResetToken(String token) {
        return tokenRepository.findValidToken(token, LocalDateTime.now())
            .map(PasswordResetToken::isValid)
            .orElse(false);
    }

    private String generateResetToken() {
        return UUID.randomUUID().toString();
    }

    // Limits reset requests per hour to reduce abuse
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

    private String getUserDisplayName(User user) {
        if (user.getName() != null && !user.getName().trim().isEmpty()) {
            return user.getName().split(" ")[0];
        }
        if (user.getUsername() != null && !user.getUsername().trim().isEmpty()) {
            return user.getUsername();
        }
        return "User";
    }

    @Transactional
    public int cleanupExpiredTokens() {
        int deleted = tokenRepository.deleteExpiredTokens(LocalDateTime.now());
        if (deleted > 0) {
            log.info("Cleaned up {} expired password reset tokens", deleted);
        }
        return deleted;
    }
}
