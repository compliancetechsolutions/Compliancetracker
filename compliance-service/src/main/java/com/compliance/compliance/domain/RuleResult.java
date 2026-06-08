package com.compliance.compliance.domain;

import lombok.Data;

@Data
public class RuleResult {

	private boolean passed;
	private String ruleName;
	private String message;

}