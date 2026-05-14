package com.compliance.entity.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import com.compliance.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
@Entity
@Table(name = "entity_documents", schema = "entity_schema")
@Data
@EqualsAndHashCode(callSuper = true)

@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EntityDocument extends BaseEntity {

	 @Id
	    @Column(name = "document_id")
	    private UUID documentId;

	    @Column(name = "entity_id")
	    private UUID entityId;

	    @Column(name = "document_name")
	    private String documentName;

	    @Column(name = "file_location")
	    private String fileLocation;

	    @Column(name = "uploaded_at")
	    private LocalDateTime uploadedAt;
}
