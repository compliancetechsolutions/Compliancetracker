package com.compliance.common.kafka.event;

public class UserEventType {
	private UserEventType() {
	}

	public static final String CREATE_USER = "CREATE_USER";
	public static final String UPDATE_USER = "UPDATE_USER";
	public static final String DELETE_USER = "DELETE_USER";
	public static final String BULK_CREATE_USERS = "BULK_CREATE_USERS";
	public static final String ADD_ROLE = "ADD_ROLE";
	public static final String ADD_MULTIPLE_ROLES = "ADD_MULTIPLE_ROLES";
	public static final String REMOVE_ROLE = "REMOVE_ROLE";
	public static final String RESET_PASSWORD = "RESET_PASSWORD";
	public static final String ACTIVATE_USER = "ACTIVATE_USER";
	public static final String DEACTIVATE_USER = "DEACTIVATE_USER";

}
