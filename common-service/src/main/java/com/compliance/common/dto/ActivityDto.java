package com.compliance.common.dto;

import java.time.LocalDate;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ActivityDto extends BaseDto {
	private String activityName; // e.g., Salary Payment, PF Remittance [cite: 13]
	private LocalDate dueDate; // Updated on the 1st of each month [cite: 17]
	private LocalDate completionDate;

	// Logic for Requirement
	public String getStatus() {
		if (completionDate == null) {
			return LocalDate.now().isAfter(dueDate) ? "PENDING_DELAYED" : "UPCOMING";
		}
		return completionDate.isAfter(dueDate) ? "DELAYED" : "COMPLIED";
	}

}
