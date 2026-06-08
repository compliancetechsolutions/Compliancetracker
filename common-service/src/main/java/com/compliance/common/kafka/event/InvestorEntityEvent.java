package com.compliance.common.kafka.event;

import java.util.UUID;



import lombok.AllArgsConstructor;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class InvestorEntityEvent extends BaseEvent {

	private UUID entityId;

	private UUID userId;

	private String entityName;

	private String relationshipType;
}