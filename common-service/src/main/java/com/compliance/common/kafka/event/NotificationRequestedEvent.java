
package com.compliance.common.kafka.event;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter

@NoArgsConstructor
@AllArgsConstructor

@SuperBuilder

public class NotificationRequestedEvent extends BaseEvent {

	private UUID notificationId;
	private UUID entityId;
	private UUID userId;
	private String channel;
	private String title;
	private String message;
	private String priority;
	private LocalDateTime scheduledAt;

}
