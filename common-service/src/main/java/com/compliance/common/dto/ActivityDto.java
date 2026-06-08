package com.compliance.common.dto;

import java.time.LocalDate;

import com.compliance.common.enums.ComplianceStatus;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data transfer object for compliance activity records.
 *
 * <p><b>Fix: business logic removed from DTO layer.</b>
 *
 * <p>The original {@code ActivityDto} contained a {@code getStatus()} method
 * that implemented compliance status derivation logic directly inside the DTO:
 * <pre>
 *   public String getStatus() {
 *       if (completionDate == null) {
 *           return LocalDate.now().isAfter(dueDate) ? "PENDING_DELAYED" : "UPCOMING";
 *       }
 *       return completionDate.isAfter(dueDate) ? "DELAYED" : "COMPLIED";
 *   }
 * </pre>
 *
 * <p>This violates layered architecture for several reasons:
 * <ul>
 *   <li>DTOs are transport containers — they should carry data, not execute
 *       business rules.</li>
 *   <li>The logic uses {@code LocalDate.now()} making it non-deterministic and
 *       untestable without mocking the system clock.</li>
 *   <li>The status is a calculated field that Jackson would serialize — clients
 *       would see a {@code status} field that appears to be stored data but is
 *       actually computed on the fly, creating confusion about whether it can be
 *       used as a filter parameter.</li>
 *   <li>The string literals "PENDING_DELAYED", "UPCOMING", "DELAYED", "COMPLIED"
 *       are magic values — duplicated across the codebase with no single
 *       canonical definition.</li>
 * </ul>
 *
 * <p><b>Correct approach:</b>
 * <ol>
 *   <li>Status is computed in the service layer using
 *       {@link com.compliance.common.enums.ComplianceStatus}.</li>
 *   <li>The computed status is set on this DTO by the service before returning
 *       to the controller.</li>
 *   <li>The DTO carries it as a plain field — no computation here.</li>
 * </ol>
 */
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ActivityDto extends BaseDto {

    private String     activityName;
    private LocalDate  dueDate;
    private LocalDate  completionDate;

    /**
     * Compliance status — set by the service layer, not computed here.
     * Use {@link ComplianceStatus#derive(LocalDate, LocalDate)} to calculate.
     */
    private ComplianceStatus status;
}
