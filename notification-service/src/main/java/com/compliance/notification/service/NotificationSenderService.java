package com.compliance.notification.service;

import com.compliance.notification.entity.NotificationRecord;

public interface NotificationSenderService {

  /**
   * 
   * Sends notification.
   *
   * Implementation decides async/sync behavior.
   */
  void send(NotificationRecord notification);

}
