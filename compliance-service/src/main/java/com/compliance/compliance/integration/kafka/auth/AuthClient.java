
package com.compliance.compliance.integration.kafka.auth;

import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.annotation.KafkaListener;

import org.springframework.stereotype.Component;

import com.compliance.common.kafka.event.UserEvent;
import com.compliance.common.kafka.event.UserEventType;

@Slf4j

@Component

public class AuthClient {

	@KafkaListener(

			topics = "${app.kafka.topics.user-events}",

			groupId = "compliance-group"

	)

	public void consume(

			UserEvent event

	) {

		if (event == null ||

				event.getEventType() == null) {

			return;

		}

		switch (

		event.getEventType()

		) {

		case UserEventType.CREATE_USER:

			handleCreate(event);

			break;

		case UserEventType.UPDATE_USER:

			handleUpdate(event);

			break;

		case UserEventType.DELETE_USER:

			handleDelete(event);

			break;

		case UserEventType.RESET_PASSWORD:

			handleReset(event);

			break;

		case UserEventType.ACTIVATE_USER:

		case UserEventType.DEACTIVATE_USER:

			handleStatus(event);

			break;

		case UserEventType.ADD_ROLE:

		case UserEventType.ADD_MULTIPLE_ROLES:

		case UserEventType.REMOVE_ROLE:

			handleRole(event);

			break;

		case UserEventType.BULK_CREATE_USERS:

			handleBulk(event);

			break;

		default:

			log.warn(

					"Unsupported event {}",

					event.getEventType()

			);

		}

	}

// =====================================

	private void handleCreate(

			UserEvent event

	) {

		log.info("Create {}", event.getUserId());

	}

	private void handleUpdate(

			UserEvent event

	) {

		log.info("Update {}", event.getUserId());

	}

	private void handleDelete(

			UserEvent event

	) {

		log.info("Delete {}", event.getUserId());

	}

	private void handleReset(

			UserEvent event

	) {

		log.info("Reset {}", event.getUserId());

	}

	private void handleStatus(

			UserEvent event

	) {

		log.info("Status {}", event.getStatus());

	}

	private void handleRole(

			UserEvent event

	) {

		log.info("Roles {}", event.getRoles());

	}

	private void handleBulk(

			UserEvent event

	) {

		log.info("Bulk {}", event.getTotalUsers());

	}

}
