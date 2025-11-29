package org.abdallah.imageprocessingservice.config;

import lombok.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Objects;
import java.util.Optional;

@Configuration
public class AuditConfig {

    @Bean
    public AuditorAware<@NonNull Long> auditorProvider() {
        return () -> {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            if (auth == null || !auth.isAuthenticated() || Objects.equals(auth.getPrincipal(), "anonymousUser")) {
                return Optional.empty();
            }

            try {
                // username = token subject
                String username = auth.getName();
                // convert username to user ID if needed
                return Optional.of(1L); // TEMP: replace with real userId
            } catch (Exception e) {
                return Optional.empty();
            }
        };
    }
}
