CREATE TABLE event_schema.failed_events (

    event_id UUID PRIMARY KEY,

    event_type VARCHAR(255),

    aggregate_id UUID,

    service_name VARCHAR(255),

    retry_count INTEGER,

    status VARCHAR(50),

    timestamp TIMESTAMP,

    created_at TIMESTAMP,

    updated_at TIMESTAMP,

    topic_name VARCHAR(255),

    payload JSONB,

    error_message TEXT,

    stack_trace TEXT
);