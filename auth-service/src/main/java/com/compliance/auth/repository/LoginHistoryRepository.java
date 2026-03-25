package com.compliance.auth.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.compliance.auth.entity.LoginHistory;

public interface LoginHistoryRepository  extends JpaRepository<LoginHistory, UUID>  {
    List<LoginHistory> findByUserId(UUID userId);


}
