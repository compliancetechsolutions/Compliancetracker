CREATE TABLE compliance_schema.compliance_records (

compliance_id UUID PRIMARY KEY,

entity_id UUID NOT NULL,

activity_id UUID NOT NULL,

due_date DATE,

actual_completion_date DATE,

status VARCHAR(50),

created_at TIMESTAMP DEFAULT NOW()

);

CREATE TABLE compliance_schema.compliance_rules (

rule_id UUID PRIMARY KEY,

rule_name VARCHAR(200),

rule_condition TEXT

);

CREATE TABLE compliance_schema.compliance_activities (

activity_id UUID PRIMARY KEY,

activity_name VARCHAR(200),

description TEXT,

frequency VARCHAR(50)

);

CREATE TABLE compliance_schema.compliance_execution (

execution_id UUID PRIMARY KEY,

compliance_id UUID,

execution_result TEXT,

executed_at TIMESTAMP

);

CREATE TABLE compliance_schema.notification_queue (

notification_id UUID PRIMARY KEY,

compliance_id UUID,

status VARCHAR(50),

created_at TIMESTAMP

);