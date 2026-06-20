
package com.compliance.compliance.workflow;

import com.compliance.compliance.event.WorkflowTaskEvent;

public interface ComplianceWorkflow {

  void execute(WorkflowTaskEvent event);

}
