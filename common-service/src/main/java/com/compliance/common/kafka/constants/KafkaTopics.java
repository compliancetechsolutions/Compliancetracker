package com.compliance.common.kafka.constants;

public final class KafkaTopics {

	private KafkaTopics() {
	}

	public static final String USER_EVENTS = "user-events";
	public static final String INVESTOR_ENTITY_EVENTS = "investor-entity-event";
	public static final String REPRESENTATIVE_ENTITY_EVENTS = "representative-entity-event";
	public static final String NOTIFICATION_EVENTS = "notification-events";
	public static final String WORKFLOW_TASK = "workflow-task";
	public static final String ENTITY_EVENTS = "entity-events";
	public static final String USER_EVENTS_DLT = "user-events.DLT";
	public static final String ENTITY_DLT_GROUP = "entity-dlt-group";

}