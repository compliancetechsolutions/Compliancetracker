package com.compliance.notification.dto;
import java.util.UUID;


import lombok.Data;
import lombok.experimental.SuperBuilder;
@Data

@SuperBuilder
public class NotificationResponse {

  private UUID notificationId;
  private String status;
  
  

}