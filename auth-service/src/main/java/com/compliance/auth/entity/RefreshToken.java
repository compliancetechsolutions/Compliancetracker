package com.compliance.auth.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import com.compliance.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "refresh_tokens", schema = "auth_schema")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class RefreshToken extends BaseEntity {
	@Id
	@Column(name = "token_id")
	private UUID tokenId;

	@Column(name = "user_id")
	private UUID userId;

	@Column(columnDefinition = "TEXT")
	private String token;

	@Column(name = "expiry_time")
	private LocalDateTime expiryTime;

	private boolean revoked;

}
