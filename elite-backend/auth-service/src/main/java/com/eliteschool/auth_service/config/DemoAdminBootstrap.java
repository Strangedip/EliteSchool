package com.eliteschool.auth_service.config;

import com.eliteschool.auth_service.model.User;
import com.eliteschool.auth_service.model.enums.RoleType;
import com.eliteschool.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Ensures a first Admin exists for local demos. Skips if any ADMIN already exists.
 * Default credentials are documented in USAGE_GUIDE.md / README.md — change after first login.
 */
@Component
@Order(1)
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "app.demo-admin.enabled", havingValue = "true", matchIfMissing = true)
public class DemoAdminBootstrap implements ApplicationRunner {

    public static final String DEMO_ADMIN_USERNAME = "admin";
    public static final String DEMO_ADMIN_PASSWORD = "Admin@123";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        if (!userRepository.findByRole(RoleType.ADMIN).isEmpty()) {
            return;
        }
        if (userRepository.existsByUsername(DEMO_ADMIN_USERNAME)
                || userRepository.existsByEmail("admin@eliteschool.local")) {
            log.warn("Demo admin username/email taken but no ADMIN role found — skip bootstrap");
            return;
        }

        User admin = User.builder()
                .name("School Admin")
                .username(DEMO_ADMIN_USERNAME)
                .email("admin@eliteschool.local")
                .password(passwordEncoder.encode(DEMO_ADMIN_PASSWORD))
                .role(RoleType.ADMIN)
                .active(true)
                .emailVerified(true)
                .build();
        userRepository.save(admin);
        log.info("Bootstrapped demo Admin user '{}' — change the password after first login", DEMO_ADMIN_USERNAME);
    }
}
