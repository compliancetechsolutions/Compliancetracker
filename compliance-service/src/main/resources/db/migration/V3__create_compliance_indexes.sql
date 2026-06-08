CREATE INDEX idx_compliance_entity
ON compliance_schema.compliance_records(entity_id);

CREATE INDEX idx_compliance_status
ON compliance_schema.compliance_records(status);

CREATE INDEX idx_due_date
ON compliance_schema.compliance_records(due_date);

CREATE INDEX idx_execution_compliance
ON compliance_schema.compliance_execution(compliance_id);

CREATE INDEX idx_notification_status
ON compliance_schema.notification_queue(status);