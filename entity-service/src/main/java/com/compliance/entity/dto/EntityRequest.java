package com.compliance.entity.dto;

import java.time.LocalDate;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EntityRequest {

    @NotBlank(
        message =
        "Entity name is required"
    )
    @Size(
        min = 2,
        max = 200
    )
    private String entityName;

    private UUID entityTypeId;

    @Size(
        max = 100
    )
    @Pattern(
        regexp =
        "^[A-Za-z0-9\\-/]*$",
        message =
        "Invalid registration number"
    )
    private String registrationNumber;

    @JsonFormat(
        shape =
        JsonFormat.Shape.STRING,

        pattern =
        "yyyy-MM-dd"
    )
    private LocalDate companyStartDate;

    @Min(0)
    @Max(1000000)
    private Integer noOfEmployees;

    /*
     * GLOBAL
     * APAC
     * EMEA
     * AMERICAS
     */
    @NotBlank(
        message =
        "Region is required"
    )
    private String region;

    /*
     * IN
     * US
     * SG
     * GB
     */
    @NotBlank(
        message =
        "Country is required"
    )
    private String countryCode;

    /*
     * TN
     * CA
     * TX
     */
    private String stateCode;

    /*
     * PRIVATE_LIMITED
     * LLC
     * PUBLIC
     */
    private String legalStructure;

    /*
     * true → receives global rules
     */
    private Boolean globalEntity =
            false;

}