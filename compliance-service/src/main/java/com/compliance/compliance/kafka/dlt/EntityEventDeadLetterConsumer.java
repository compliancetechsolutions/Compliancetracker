package com.compliance.compliance.kafka.dlt;

import org.springframework.kafka.annotation.DltHandler;
import org.springframework.stereotype.Component;

import com.compliance.common.kafka.dlt.BaseDeadLetterConsumer;
import com.compliance.common.kafka.event.EntityEvent;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class EntityEventDeadLetterConsumer

extends BaseDeadLetterConsumer<EntityEvent> {


@DltHandler
public void consume(

        EntityEvent event

) {

    if (
            event == null
    ) {

        log.error(
                "ENTITY EVENT DLT EMPTY"
        );

        return;

    }

    logDlt(

            "entity-events.DLT",

            event

    );

}


}
