package com.compliance.entity.kafka.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.compliance.common.kafka.config.BaseKafkaProducerConfig;
import com.compliance.common.kafka.event.InvestorEntityEvent;


@Service
public class InvestorEntityProducer
        extends BaseKafkaProducerConfig<
                InvestorEntityEvent
                > {

    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public InvestorEntityProducer(

            KafkaTemplate<
                    String,
                    InvestorEntityEvent
                    > kafkaTemplate
    ) {

        super(kafkaTemplate);
    }

    // =====================================================
    // TOPIC NAME
    // =====================================================

    @Override
    protected String getTopicName() {

        return "investor-entity-event";
    }

    // =====================================================
    // PUBLISH
    // =====================================================

    public void publish(
            InvestorEntityEvent event
    ) {

        super.publish(

                event.getEntityId().toString(),

                event
        );
    }
}