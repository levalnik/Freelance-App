package org.levalnik.outbox.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.levalnik.kafkaEvent.bidKafkaEvent.*;
import org.levalnik.kafkaEvent.projectKafkaEvent.ProjectDeletedEvent;
import org.levalnik.outbox.repository.OutboxEventRepository;
import org.levalnik.kafka.producer.KafkaProducer;
import org.levalnik.outbox.model.OutboxEvent;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxEventPublisher {

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaProducer kafkaProducer;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedDelay = 5000)
    public void publishCreatedEvents() {
        List<OutboxEvent> events = outboxEventRepository
                .findTop100ByProcessedFalseAndEventTypeOrderByCreatedAtAsc("BidCreatedEvent");

        for (OutboxEvent event : events) {
            try {
                BidCreatedEvent createdEvent = objectMapper.readValue(event.getPayload(), BidCreatedEvent.class);
                kafkaProducer.sendBidCreatedEvent(createdEvent);
                event.setProcessed(true);
                outboxEventRepository.save(event);
                log.info("Published outbox event [id={}, type={}]", event.getId(), event.getEventType());
            } catch (Exception e) {
                log.error("Failed to publish outbox event [id={}]: {}", event.getId(), e.getMessage());
            }
        }
    }

    @Scheduled(fixedDelay = 5000)
    public void publishUpdatedEvents() {
        List<OutboxEvent> events = outboxEventRepository
                .findTop100ByProcessedFalseAndEventTypeOrderByCreatedAtAsc("BidUpdatedEvent");

        for (OutboxEvent event : events) {
            try {
                BidStatusUpdatedEvent updatedEvent = objectMapper.readValue(event.getPayload(), BidStatusUpdatedEvent.class);
                kafkaProducer.sendBidStatusUpdatedEvent(updatedEvent);
                event.setProcessed(true);
                outboxEventRepository.save(event);
                log.info("Published outbox event [id={}, type={}]", event.getId(), event.getEventType());
            } catch (Exception e) {
                log.error("Failed to publish outbox event [id={}]: {}", event.getId(), e.getMessage());
            }
        }
    }
}