package com.compliance.common.kafka.dlt;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class BaseDeadLetterConsumer<T> {

  protected void logDlt(

      String topic,

      T event

  ) {

    log.error(

        "DLT topic={} payload={}",

        topic,

        event

    );

  }

}
