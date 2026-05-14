package com.compliance.enums;

import java.time.LocalDate;

public enum ComplianceStatus {

    UPCOMING,
    PENDING_DELAYED,
    COMPLIED,
    DELAYED;

    // 🔥 BUSINESS LOGIC (CORRECT PLACE)
    public static ComplianceStatus derive(LocalDate dueDate, LocalDate completionDate) {

        if (completionDate == null) {
            return LocalDate.now().isAfter(dueDate)
                    ? PENDING_DELAYED
                    : UPCOMING;
        }

        return completionDate.isAfter(dueDate)
                ? DELAYED
                : COMPLIED;
    }
}