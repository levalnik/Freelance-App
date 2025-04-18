package org.levalnik.outbox.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.levalnik.outbox.repository.OutboxEventRepository;
import org.levalnik.kafka.producer.KafkaProducer;
import org.levalnik.kafkaEvent.userKafkaEvent.UserCreatedEvent;
import org.levalnik.kafkaEvent.userKafkaEvent.UserUpdatedEvent;
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
                .findTop100ByProcessedFalseAndEventTypeOrderByCreatedAtAsc("UserCreatedEvent");

        for (OutboxEvent event : events) {
            try {
                UserCreatedEvent createdEvent = objectMapper.readValue(event.getPayload(), UserCreatedEvent.class);
                kafkaProducer.sendUserCreatedEvent(createdEvent);
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
                .findTop100ByProcessedFalseAndEventTypeOrderByCreatedAtAsc("UserUpdatedEvent");

        for (OutboxEvent event : events) {
            try {
                UserUpdatedEvent updatedEvent = objectMapper.readValue(event.getPayload(), UserUpdatedEvent.class);
                kafkaProducer.sendUserUpdatedEvent(updatedEvent);
                event.setProcessed(true);
                outboxEventRepository.save(event);
                log.info("Published outbox event [id={}, type={}]", event.getId(), event.getEventType());
            } catch (Exception e) {
                log.error("Failed to publish outbox event [id={}]: {}", event.getId(), e.getMessage());
            }
        }
    }

    @Scheduled(fixedDelay = 5000)
    public void publishDeletedEvents() {
        List<OutboxEvent> events = outboxEventRepository
                .findTop100ByProcessedFalseAndEventTypeOrderByCreatedAtAsc("UserDeletedEvent");

        for (OutboxEvent event : events) {
            try {
                UserUpdatedEvent updatedEvent = objectMapper.readValue(event.getPayload(), UserUpdatedEvent.class);
                kafkaProducer.sendUserUpdatedEvent(updatedEvent);
                event.setProcessed(true);
                outboxEventRepository.save(event);
                log.info("Published outbox event [id={}, type={}]", event.getId(), event.getEventType());
            } catch (Exception e) {
                log.error("Failed to publish outbox event [id={}]: {}", event.getId(), e.getMessage());
            }
        }
    }
}