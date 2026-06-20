package com.compliance.notification.integration.senderimpl;

import org.springframework.stereotype.Component;

import com.compliance.notification.integration.sender.SmsGateway;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SmsGatewayImpl  implements SmsGateway {

  @Override
  public void send(String phone, String message) {

    log.info("[SMS] sent to={}", mask(phone));

  }

  private String mask(String phone) {

    if (phone == null || phone.length() < 4) {

      return "***";

    }

    return "******" + phone.substring(phone.length() - 4);

  }
}
