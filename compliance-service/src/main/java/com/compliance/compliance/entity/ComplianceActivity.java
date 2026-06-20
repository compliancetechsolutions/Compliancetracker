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

@Table(name = "compliance_activities", schema = "compliance_schema")

public class ComplianceActivity

    extends BaseEntity {

  @Id

  @Column(name = "activity_id")

  private UUID activityId;

  @Column(name = "activity_name")

  private String activityName;

  @Column(name = "description")

  private String description;

@Column(
name="frequency"
)

private String frequency;

}
