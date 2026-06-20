package com.compliance.notification.integration.sender;

public interface SmsGateway {

  void send(String phone, String message);

}
