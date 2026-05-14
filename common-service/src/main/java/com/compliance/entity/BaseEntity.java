package com.compliance.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor; // ✅ add
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor; // ✅ add
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor // ✅ add — fixes "implicit super constructor undefined"
@AllArgsConstructor // ✅ add — needed by child @AllArgsConstructor
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

	@Builder.Default
	@Version
	@Column(name = "version", nullable = false)
	private Long version = 0L;

	@CreatedDate
	@Column(name = "created_at", updatable = false, nullable = false, columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
	private LocalDateTime createdAt;

	@LastModifiedDate
	@Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
	private LocalDateTime updatedAt;

	@CreatedBy
	@Column(name = "created_by", updatable = false, length = 100)
	private String createdBy;

	@LastModifiedBy
	@Column(name = "updated_by", length = 100)
	private String updatedBy;

	@Builder.Default
	@Column(name = "is_deleted", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
	protected Boolean isDeleted = false;

	@PrePersist
	public void prePersist() {

		LocalDateTime now = LocalDateTime.now();

		if (createdAt == null) {
			createdAt = now;
		}

		if (updatedAt == null) {
			updatedAt = now;
		}

		if (version == null) {
			version = 0L;
		}

		if (isDeleted == null) {
			isDeleted = false;
		}
	}

}