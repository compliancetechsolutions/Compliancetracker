package com.compliance.apigateway.config;

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;
import com.compliance.apigateway.util.Role;

/**
 * Defines which roles are permitted to access each route prefix.
 *
 * <p>
 * <b>Fix from original:</b> this bean was defined but never injected into
 * {@link com.compliance.apigateway.filter.JwtAuthenticationFilter} —
 * route-level RBAC was silently unenforced. It is now injected and called in
 * the filter.
 *
 * <p>
 * <b>Design:</b> an empty allowed-roles list means "any authenticated user may
 * access this path" (auth is still required — just no role restriction). Paths
 * not matched by any prefix fall into this open category.
 *
 * <p>
 * For production, consider externalising this map into a YAML config file so
 * routing rules can be updated without recompiling.
 *
 * <pre>
 * Route prefix → Allowed roles
 * ──────────────────────────────────────────────────
 * /auth/users   → ADMIN only           (user management)
 * /entity       → ADMIN, COMPANY_REP   (entity management)
 * /compliance   → ADMIN, COMPANY_REP   (compliance tracker)
 * /report       → ADMIN, INVESTOR      (reports)
 * /audit        → ADMIN only           (audit trail)
 * /archive      → ADMIN only           (archival)
 * /investor     → ADMIN, INVESTOR      (investor views)
 * /initiator    → ADMIN, COMPANY_REP   (workflow initiation)
 * /notification → ADMIN, COMPANY_REP, INVESTOR  (notifications)
 * </pre>
 */
@Component
public class RouteRoleConfig {

  private static final String ADMIN = "ROLE_" + Role.ADMIN.name();

  private static final String COMPANY = "ROLE_" + Role.COMPANY_REPRESENTATIVE.name();

  private static final String INVESTOR = "ROLE_" + Role.INVESTOR.name();

  /**
   * Route prefix → list of roles that may access it. Evaluated in insertion order
   * — first match wins.
   *
   * <p>
   * Using {@code Map.of} preserves declaration order in Java 21 (LinkedHashMap
   * internally). For > 10 entries use a LinkedHashMap explicitly.
   */
  private final Map<String, List<String>> roleMappings = Map.ofEntries(
      // User management — admin only
      Map.entry("/auth/users", List.of(ADMIN)),

      // Entity management — admin or company rep
      Map.entry("/entity", List.of(ADMIN, COMPANY)),

      // Compliance tracker — admin or company rep
      Map.entry("/compliance", List.of(ADMIN, COMPANY)),

      // Reports — admin or investor
      Map.entry("/report", List.of(ADMIN, INVESTOR)),

      // Audit trail — admin only
      Map.entry("/audit", List.of(ADMIN)),

      // Archive — admin only
      Map.entry("/archive", List.of(ADMIN)),

      // Investor portal — admin or investor
      Map.entry("/investor", List.of(ADMIN, INVESTOR)),

      // Workflow initiation — admin or company rep
      Map.entry("/initiator", List.of(ADMIN, COMPANY)),

      // Notifications — all authenticated roles
      Map.entry("/notification", List.of(ADMIN, COMPANY, INVESTOR)));

  /**
   * Returns the list of roles permitted for the given request path. Returns an
   * empty list if the path is not in the map, meaning any authenticated user is
   * allowed.
   *
   * @param path the request URI path (e.g. "/entity/api/entities")
   * @return list of permitted role names, or empty list for "any authenticated"
   */
  public List<String> getAllowedRoles(String path) {
    return roleMappings.entrySet().stream().filter(entry -> path.startsWith(entry.getKey()))
        .map(Map.Entry::getValue).findFirst().orElse(List.of());
  }
}
