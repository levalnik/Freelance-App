package org.levalnik.kafka.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.levalnik.kafkaEvent.bidKafkaEvent.*;
import org.levalnik.kafka.config.KafkaConfig;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendBidCreatedEvent(BidCreatedEvent event) {
        log.info("Sending bid created event: {}", event);
        kafkaTemplate.send(KafkaConfig.BID_CREATED_TOPIC, event.getBidId().toString(), event);
    }

    public void sendBidStatusUpdatedEvent(BidStatusUpdatedEvent event) {
        log.info("Sending bid status updated event: {}", event);
        kafkaTemplate.send(KafkaConfig.BID_STATUS_UPDATED_TOPIC, event.getBidId().toString(), event);
    }
} 