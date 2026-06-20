
package com.compliance.common.security;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.compliance.common.enums.Role;

import java.util.Objects;

/**
 * Thread-local user context populated by the service-level
 * {@code JwtAuthFilter}.
 *
 * <p>
 * The gateway injects {@code X-User-Id} into requests. The filter sets that
 * value as {@link Authentication#getName()}, so {@link #getUserId()} returns
 * the correct UUID.
 *
 * <p>
 * All methods throw {@link IllegalStateException} if called outside of an
 * authenticated request context — this is intentional so misconfigured
 * endpoints fail fast rather than silently using a wrong identity.
 */

public final class UserContext {

  private UserContext() {
  }

  // ── User ID ──────────────────────────────────────────────────────────────

  public static UUID getUserId() {
    return getAuthentication().map(Authentication::getName).map(UserContext::parseUuid)
        .orElseThrow(() -> new IllegalStateException("No authenticated user found in security context"));
  }

  // ── Username (from credential slot populated by filter) ──────────────────

  public static String getUsername() {
    return getAuthentication().map(auth -> {
      Object creds = auth.getCredentials();
      return creds instanceof String s ? s : auth.getName();
    }).orElseThrow(() -> new IllegalStateException("No username in security context"));
  }

  // ── Primary role ─────────────────────────────────────────────────────────

  public static Role getRole() {
    return getAuthentication().map(Authentication::getAuthorities).flatMap(UserContext::extractPrimaryRole)
        .orElseThrow(() -> new IllegalStateException("No role in security context"));
  }

  /** All roles assigned to the current user. */

  public static List<Role> getRoles() {
    return getAuthentication().map(Authentication::getAuthorities).stream().flatMap(Collection::stream)
        .map(GrantedAuthority::getAuthority).map(r -> r.replace("ROLE_", "")).map(r -> {
          try {
            return Role.valueOf(r);
          } catch (IllegalArgumentException e) {
            return null;
          }
        }).filter(r -> r != null).toList();
  }

  /** Convenience: returns true if user has the given role or higher. */
  public static boolean hasMinimumRole(Role required) {

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();

    if (auth == null) {

      System.out.println("AUTH IS NULL");

      return false;
    }

    System.out.println("Required Role = " + required);

    auth.getAuthorities().forEach(a -> System.out.println("Authority = " + a.getAuthority()));

    return auth.getAuthorities().stream()

        .map(GrantedAuthority::getAuthority)

        .map(role -> role.replace("ROLE_", ""))

        .filter(role -> !role.equals("ANONYMOUS"))

        .map(role -> {
          try {
            return Role.valueOf(role);
          } catch (Exception e) {
            return null;
          }
        })

        .filter(Objects::nonNull)

        .anyMatch(role -> {
          System.out.println("Parsed Role = " + role);

          return role.hasAccess(required);
        });
  }

  // ── Helpers

  private static Optional<Authentication> getAuthentication() {

      Authentication auth =
              SecurityContextHolder.getContext()
                      .getAuthentication();

      if (auth == null
              || !auth.isAuthenticated()) {

          return Optional.empty();
      }

      boolean anonymous =
              auth.getAuthorities().stream()

                      .map(GrantedAuthority::getAuthority)

                      .anyMatch("ROLE_ANONYMOUS"::equals);

      return anonymous
              ? Optional.empty()
              : Optional.of(auth);
  }
  private static UUID parseUuid(String value) {
    try {
      return UUID.fromString(value);
    } catch (IllegalArgumentException e) {
      throw new IllegalStateException("Invalid UUID in authentication principal: " + value, e);
    }
  }

  private static Optional<Role> extractPrimaryRole(Collection<? extends GrantedAuthority> authorities) {

    return authorities.stream()

        .map(GrantedAuthority::getAuthority)

        .map(role -> role.replace("ROLE_", ""))

        // Ignore Spring anonymous role
        .filter(role -> !role.equals("ANONYMOUS"))

        .map(role -> {
          try {
            return Role.valueOf(role);
          } catch (IllegalArgumentException e) {
            return null;
          }
        })

        .filter(Objects::nonNull)

        .max(Comparator.comparingInt(Role::getLevel));
  }
}
