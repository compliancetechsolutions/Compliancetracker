ALTER TABLE compliance_schema.compliance_records
PARTITION BY RANGE (due_date);