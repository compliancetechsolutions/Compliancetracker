package com.compliance.common.config;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("auditorProvider") 
public class AuditorAwareImpl implements AuditorAware<String> {

  private static final String SYSTEM = "SYSTEM";

  @Override
  public Optional<String> getCurrentAuditor() {

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();

    if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
      return Optional.of(SYSTEM);
    }

    // FIX: Use auth.getName() directly instead of UserContext.getUserId().
    // UserContext.getUserId() calls UUID.fromString(name) which throws
    // IllegalStateException if the principal is not a valid UUID
    // (e.g. "123" from a direct Swagger call, or a username string).
    // That exception propagates up through the @Transactional boundary
    // and rolls back the entire transaction — silently killing the save.
    String name = auth.getName();
    if (name == null || name.isBlank()) {
      return Optional.of(SYSTEM);
    }
    return Optional.of(name);
  }
}