package com.compliance.entity.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BulkEntityResponse {

  private int totalRequested;
  private int createdCount;
  private int skippedCount;
  private List<EntityResponse> createdEntities;
  private List<String> skippedEntities;
  private String message;
}