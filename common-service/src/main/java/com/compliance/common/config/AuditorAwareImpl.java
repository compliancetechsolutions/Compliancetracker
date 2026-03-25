package com.compliance.common.config;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;

public class AuditorAwareImpl implements AuditorAware<String> {
	@Override
	public Optional<String> getCurrentAuditor() {

		// TODO: integrate with Spring Security later
		return Optional.of("SYSTEM");

		// Advanced:
		// return
		// Optional.of(SecurityContextHolder.getContext().getAuthentication().getName());
	}

}
