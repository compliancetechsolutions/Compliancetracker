package com.compliance.common.kafka.event;

import java.util.UUID;

import lombok.Getter;

import lombok.Setter;

import lombok.NoArgsConstructor;

import lombok.AllArgsConstructor;

import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class EntityEvent

    extends BaseEvent {

  /**
       * 
       */
      private static final long serialVersionUID = 318651055804509693L;

  private UUID entityId;

  private UUID userId;

  private String relationshipType;

  private String entityName;

  private String email;

}
