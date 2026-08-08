package com.eliteschool.common_utils.security;

import com.eliteschool.common_utils.exception.AppException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

public final class GatewayAuth {

    private GatewayAuth() {}

    public static String role(HttpServletRequest request) {
        String role = request.getHeader(GatewayHeaders.ROLE);
        return StringUtils.hasText(role) ? role.trim().toUpperCase(Locale.ROOT) : null;
    }

    public static String username(HttpServletRequest request) {
        return request.getHeader(GatewayHeaders.USERNAME);
    }

    public static Optional<UUID> userId(HttpServletRequest request) {
        String raw = request.getHeader(GatewayHeaders.USER_ID);
        if (!StringUtils.hasText(raw)) {
            return Optional.empty();
        }
        try {
            return Optional.of(UUID.fromString(raw.trim()));
        } catch (IllegalArgumentException ex) {
            return Optional.empty();
        }
    }

    public static boolean isInternal(HttpServletRequest request) {
        return StringUtils.hasText(request.getHeader(GatewayHeaders.INTERNAL_SERVICE));
    }

    public static void requireRoles(HttpServletRequest request, String... allowedRoles) {
        String role = role(request);
        if (role == null) {
            throw forbidden("Missing gateway identity. Requests must go through the API gateway.");
        }
        boolean allowed = Arrays.stream(allowedRoles)
                .map(r -> r.toUpperCase(Locale.ROOT))
                .anyMatch(r -> r.equals(role));
        if (!allowed) {
            throw forbidden("Role '" + role + "' is not allowed for this operation.");
        }
    }

    public static void requireSelfOrRoles(HttpServletRequest request, UUID targetUserId, String... staffRoles) {
        Optional<UUID> callerId = userId(request);
        if (callerId.isPresent() && callerId.get().equals(targetUserId)) {
            return;
        }
        requireRoles(request, staffRoles);
    }

    public static UUID requireUserId(HttpServletRequest request) {
        return userId(request).orElseThrow(() ->
                forbidden("Missing user id from gateway. Re-login to refresh your token."));
    }

    public static void requireInternal(HttpServletRequest request) {
        if (!isInternal(request)) {
            throw forbidden("This endpoint is for internal service calls only.");
        }
    }

    private static AppException forbidden(String message) {
        return new AppException(message, message, "FORBIDDEN", HttpStatus.FORBIDDEN);
    }
}
