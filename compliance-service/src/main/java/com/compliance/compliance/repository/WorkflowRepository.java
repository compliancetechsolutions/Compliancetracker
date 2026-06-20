
package com.compliance.compliance.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.compliance.common.repository.BaseRepository;
import com.compliance.compliance.entity.ComplianceWorkflowTask;

@Repository

public interface WorkflowRepository

    extends

    BaseRepository<ComplianceWorkflowTask, UUID> {

  List<ComplianceWorkflowTask>

      findByComplianceId(

          UUID complianceId

  );

  List<ComplianceWorkflowTask> findByTaskStatus(String taskStatus);

  List<ComplianceWorkflowTask> findByWorkflowId(String workflowId);

}
