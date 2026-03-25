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
@Table(name = "login_history", schema = "auth_schema")
@Data
@EqualsAndHashCode(callSuper = true)
public class LoginHistory extends BaseEntity {

	@Id
	@Column(name = "login_id")
	private UUID loginId;

	@Column(name = "user_id")
	private UUID userId;

	private LocalDateTime loginTime;
	private String ipAddress;
	private Boolean success;
	
	
	// ✅ SUCCESS FACTORY
    public static LoginHistory success(UUID userId, String ip) {
        LoginHistory log = new LoginHistory();
        log.setLoginId(UUID.randomUUID());
        log.setUserId(userId);
        log.setLoginTime(LocalDateTime.now());
        log.setIpAddress(ip);
        log.setSuccess(true);
        return log;
    }

    // ✅ FAILED FACTORY
    public static LoginHistory failed(String username, String ip) {
        LoginHistory log = new LoginHistory();
        log.setLoginId(UUID.randomUUID());
        log.setUserId(null); // user unknown
        log.setLoginTime(LocalDateTime.now());
        log.setIpAddress(ip);
        log.setSuccess(false);
        return log;
    }
	
	
}
