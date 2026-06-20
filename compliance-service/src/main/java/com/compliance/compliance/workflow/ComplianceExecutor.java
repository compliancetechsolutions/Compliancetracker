
package com.compliance.compliance.workflow;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import com.compliance.compliance.event.WorkflowTaskEvent;

@Slf4j

@Service

public class ComplianceExecutor

    implements ComplianceWorkflow {

  @Override

  public void execute(WorkflowTaskEvent event) {

    log.info("Executing workflow {}", event.getWorkflowId());

  }

}
