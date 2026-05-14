package com.compliance.auth.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import com.compliance.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "login_history", schema = "auth_schema")
@Getter
@Setter
@NoArgsConstructor
public class LoginHistory extends BaseEntity {

    @Id
    @Column(name = "login_id")
    private UUID loginId;

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "login_time", nullable = false)
    private LocalDateTime loginTime;

    @Column(name = "ip_address", length = 100)
    private String ipAddress;

    @Column(nullable = false)
    private Boolean success;

    public static LoginHistory success(UUID userId, String ip) {
        LoginHistory h = new LoginHistory();
        h.setLoginId(UUID.randomUUID());
        h.setUserId(userId);
        h.setLoginTime(LocalDateTime.now());
        h.setIpAddress(ip);
        h.setSuccess(true);
        return h;
    }

    public static LoginHistory failed(UUID userId, String ip) {
        LoginHistory h = new LoginHistory();
        h.setLoginId(UUID.randomUUID());
        h.setUserId(userId);
        h.setLoginTime(LocalDateTime.now());
        h.setIpAddress(ip);
        h.setSuccess(false);
        return h;
    }
}
