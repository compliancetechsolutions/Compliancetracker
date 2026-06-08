CREATE TABLE compliance_schema.notification_retry (

retry_id UUID PRIMARY KEY,

notification_id UUID,

retry_count INTEGER,

next_retry TIMESTAMP

);