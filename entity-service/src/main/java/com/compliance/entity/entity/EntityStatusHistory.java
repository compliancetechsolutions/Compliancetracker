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
@Table(name = "entity_status_history", schema = "entity_schema")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EntityStatusHistory extends BaseEntity {
	@Id
	@Column(name = "status_history_id")
	private UUID statusHistoryId;

	@Column(name = "entity_id")
	private UUID entityId;

	@Column(name = "old_status")
	private String oldStatus;

	@Column(name = "new_status")
	private String newStatus;

	@Column(name = "changed_at")
	private LocalDateTime changedAt;

}
