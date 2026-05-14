package com.compliance.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

/**
 * Platform base repository.
 *
 * <p><b>Why no findByIdAndIsDeletedFalse / findAllByIsDeletedFalse here:</b>
 *
 * <p>Spring Data derives queries by introspecting the <em>concrete entity type</em>
 * bound at the leaf repository. Declaring derived query methods on a
 * {@code @NoRepositoryBean} interface causes {@code PropertyReferenceException}
 * at application startup for any repository whose entity does not have the
 * exact field name {@code isDeleted} — which breaks non-soft-delete repositories
 * and any entity with a differently named flag.
 *
 * <p>Soft-delete filtering is handled automatically at the Hibernate level via
 * {@code @SQLRestriction("is_deleted = false")} on each entity class that
 * supports soft deletes. This means every standard Spring Data method —
 * {@code findById()}, {@code findAll()}, {@code existsById()} — automatically
 * excludes soft-deleted rows with no extra code required here.
 *
 * <p>If a specific repository needs to <em>include</em> deleted records (e.g.
 * an admin audit endpoint), it can declare that query explicitly in its own
 * interface using {@code @Query} with {@code nativeQuery = true} or JPQL with
 * a manual filter, overriding the {@code @SQLRestriction} restriction.
 *
 * <p>{@link JpaSpecificationExecutor} is included to support dynamic
 * Specification-based queries (filtering, search) without boilerplate.
 */
@NoRepositoryBean
public interface BaseRepository<T, ID extends Serializable>
        extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {

    // No shared derived query methods declared here.
    // See class-level Javadoc for the reasoning.
}
