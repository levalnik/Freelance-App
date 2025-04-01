package org.levalnik.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.levalnik.DTO.events.BidCreatedEvent;
import org.levalnik.DTO.events.BidStatusUpdatedEvent;
import org.levalnik.config.KafkaConfig;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducerService {

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