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

@Table(name = "compliance_documents", schema = "compliance_schema")

public class ComplianceDocument

    extends BaseEntity {

  @Id

  @Column(name = "document_id")

  private UUID documentId;

  @Column(name = "compliance_id")

  private UUID complianceId;

  @Column(name = "document_name")

  private String documentName;

  @Column(name = "file_path")

  private String filePath;

}
