package com.compliance.auth.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleAssignmentResultDto {
   private List<String> added;
      private List<String> skipped;
      private List<String> notFound;

}
