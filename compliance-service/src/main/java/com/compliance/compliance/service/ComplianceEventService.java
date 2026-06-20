package com.compliance.compliance.service;

import com.compliance.common.kafka.event.EntityEvent;

public interface ComplianceEventService {
  void processEntity(EntityEvent event);
  void updateEntity(EntityEvent event);
  

}
