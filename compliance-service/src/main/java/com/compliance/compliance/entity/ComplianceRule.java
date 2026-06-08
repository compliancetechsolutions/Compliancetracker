package com.compliance.compliance.entity;
import java.util.UUID;
import com.compliance.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity

@Getter
@Setter

@Builder

@NoArgsConstructor
@AllArgsConstructor

@Table(
name = "compliance_rules",
schema = "compliance_schema"
)

public class ComplianceRule

extends BaseEntity {


// =====================================
// PRIMARY KEY
// =====================================

@Id

@Column(
name = "rule_id",
nullable = false
)

private UUID ruleId;



// =====================================
// RULE DETAILS
// =====================================

@Column(
name = "rule_name",
nullable = false,
length = 255
)

private String ruleName;



@Column(
name = "rule_condition",
columnDefinition = "TEXT"
)

private String ruleCondition;



// =====================================
// FLAGS
// =====================================

@Column(
name = "mandatory"
)

private Boolean mandatory;



@Column(
name = "active"
)

private Boolean active;



// =====================================
// TARGET
// =====================================

@Column(
name = "entity_type",
length = 100
)

private String entityType;



@Column(
name = "country_code",
length = 20
)

private String countryCode;



@Column(
name = "region",
length = 100
)

private String region;

}

