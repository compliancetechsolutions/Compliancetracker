package com.compliance.auth.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import com.compliance.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "user_sessions", schema = "auth_schema")
@Data
@EqualsAndHashCode(callSuper = true)
public class UserSession extends BaseEntity {
	@Id
	@Column(name = "session_id")
	private UUID sessionId;

	@Column(name = "user_id")
	private UUID userId;

	private LocalDateTime sessionStart;
	private LocalDateTime sessionEnd;
	private String jwtToken;
	 private Boolean active;
	 
	 
	 
	 

}
