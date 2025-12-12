package com.eliteschool.auth_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

/**
 * Service for sending emails (password reset, notifications, etc.)
 * Uses Thymeleaf templates for professional HTML emails
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username:noreply@eliteschool.com}")
    private String fromEmail;

    @Value("${spring.mail.properties.mail.from.name:EliteSchool}")
    private String fromName;

    @Value("${app.password-reset.frontend-url:http://localhost:4200}")
    private String frontendUrl;

    /**
     * Send password reset email with token link
     * Async to avoid blocking the API response
     */
    @Async
    public void sendPasswordResetEmail(String toEmail, String resetToken, String recipientName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, fromName);
            helper.setTo(toEmail);
            helper.setSubject("Password Reset Request - EliteSchool");

            // Build reset link
            String resetLink = String.format("%s/reset-password?token=%s", frontendUrl, resetToken);

            // Create email content from Thymeleaf template
            Context context = new Context();
            context.setVariable("name", recipientName);
            context.setVariable("resetToken", resetToken);
            context.setVariable("resetLink", resetLink);
            context.setVariable("expiryMinutes", 30);
            context.setVariable("currentYear", java.time.Year.now().getValue());

            String htmlContent = templateEngine.process("email/reset-password", context);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Password reset email sent successfully to: {}", toEmail);

        } catch (MessagingException e) {
            log.error("Failed to send password reset email to: {} - MessagingException: {}", toEmail, e.getMessage());
            throw new RuntimeException("Failed to send email", e);
        } catch (Exception e) {
            log.error("Unexpected error while sending email to: {} - {}: {}", toEmail, e.getClass().getSimpleName(), e.getMessage());
            throw new RuntimeException("Failed to send email", e);
        }
    }

    /**
     * Send confirmation email after successful password reset
     */
    @Async
    public void sendPasswordResetSuccessEmail(String toEmail, String recipientName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, fromName);
            helper.setTo(toEmail);
            helper.setSubject("Password Reset Successful - EliteSchool");

            Context context = new Context();
            context.setVariable("name", recipientName);
            context.setVariable("currentYear", java.time.Year.now().getValue());

            String htmlContent = templateEngine.process("email/password-reset-success", context);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Password reset success email sent to: {}", toEmail);

        } catch (Exception e) {
            log.error("Failed to send password reset success email to: {} - {}", toEmail, e.getMessage());
            // Don't throw exception - this is just a confirmation email
            // User already successfully reset password, no need to fail the operation
        }
    }

    /**
     * Send welcome email to new users (can be used for user registration)
     */
    @Async
    public void sendWelcomeEmail(String toEmail, String recipientName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, fromName);
            helper.setTo(toEmail);
            helper.setSubject("Welcome to EliteSchool!");

            Context context = new Context();
            context.setVariable("name", recipientName);
            context.setVariable("currentYear", java.time.Year.now().getValue());

            String htmlContent = templateEngine.process("email/welcome", context);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Welcome email sent to: {}", toEmail);

        } catch (Exception e) {
            log.error("Failed to send welcome email to: {} - {}", toEmail, e.getMessage());
            // Don't throw exception - welcome email is optional
        }
    }
}

