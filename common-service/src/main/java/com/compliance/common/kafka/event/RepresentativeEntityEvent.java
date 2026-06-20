package com.compliance.common.kafka.event;

import java.util.UUID;



import lombok.AllArgsConstructor;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = false)
@SuperBuilder

@NoArgsConstructor
@AllArgsConstructor
public class RepresentativeEntityEvent extends BaseEvent {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private UUID entityId;

  private UUID userId;

  private String entityName;

  private String relationshipType;
}