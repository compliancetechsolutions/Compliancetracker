package com.compliance.notification.integration.sender;

import com.compliance.enums.NotificationChannel;

import com.compliance.notification.entity.NotificationRecord;

public interface NotificationSender {

  NotificationChannel supports();
  void send(NotificationRecord record);

}



