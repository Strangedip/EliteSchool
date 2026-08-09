package com.eliteschool.auth_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.properties.mail.from.email:${spring.mail.username}}")
    private String fromEmail;

    @Value("${spring.mail.properties.mail.from.name:EliteSchool Support}")
    private String fromName;

    @Value("${app.password-reset.frontend-url:http://localhost:4200}")
    private String frontendUrl;

    @Value("${app.password-reset.token-expiry-minutes:30}")
    private int tokenExpiryMinutes;

    /**
     * Sent synchronously so SMTP failures surface to PasswordResetService
     * when credentials / host are wrong. With valid EMAIL_* settings this delivers
     * the reset link immediately.
     */
    public void sendPasswordResetEmail(String toEmail, String resetToken, String recipientName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, fromName);
            helper.setTo(toEmail);
            helper.setSubject("Password Reset Request - EliteSchool");

            String resetLink = String.format("%s/reset-password?token=%s", frontendUrl, resetToken);

            Context context = new Context();
            context.setVariable("name", recipientName);
            context.setVariable("resetToken", resetToken);
            context.setVariable("resetLink", resetLink);
            context.setVariable("expiryMinutes", tokenExpiryMinutes);
            context.setVariable("currentYear", java.time.Year.now().getValue());

            helper.setText(templateEngine.process("email/reset-password", context), true);
            mailSender.send(message);
            log.info("Password reset email sent to {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send password reset email to {} via {}: {}",
                    toEmail, fromEmail, e.getMessage());
            throw new IllegalStateException(
                    "SMTP send failed. Set EMAIL_HOST/PORT/USERNAME/PASSWORD (Gmail needs an App Password).",
                    e);
        }
    }

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

            helper.setText(templateEngine.process("email/password-reset-success", context), true);
            mailSender.send(message);
            log.info("Password reset success email sent to {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send password reset success email to {}: {}", toEmail, e.getMessage());
        }
    }

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

            helper.setText(templateEngine.process("email/welcome", context), true);
            mailSender.send(message);
            log.info("Welcome email sent to {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send welcome email to {}: {}", toEmail, e.getMessage());
        }
    }
}
