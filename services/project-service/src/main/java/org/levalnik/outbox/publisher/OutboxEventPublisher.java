package org.levalnik.outbox.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.levalnik.kafkaEvent.projectKafkaEvent.ProjectCreatedEvent;
import org.levalnik.kafkaEvent.projectKafkaEvent.ProjectDeletedEvent;
import org.levalnik.kafkaEvent.projectKafkaEvent.ProjectUpdatedEvent;
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
                .findTop100ByProcessedFalseAndEventTypeOrderByCreatedAtAsc("ProjectCreatedEvent");

        for (OutboxEvent event : events) {
            try {
                ProjectCreatedEvent createdEvent = objectMapper.readValue(event.getPayload(), ProjectCreatedEvent.class);
                kafkaProducer.sendProjectCreatedEvent(createdEvent);
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
                .findTop100ByProcessedFalseAndEventTypeOrderByCreatedAtAsc("ProjectUpdatedEvent");

        for (OutboxEvent event : events) {
            try {
                ProjectUpdatedEvent updatedEvent = objectMapper.readValue(event.getPayload(), ProjectUpdatedEvent.class);
                kafkaProducer.sendProjectUpdatedEvent(updatedEvent);
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
                .findTop100ByProcessedFalseAndEventTypeOrderByCreatedAtAsc("ProjectDeletedEvent");

        for (OutboxEvent event : events) {
            try {
                ProjectDeletedEvent deletedEvent = objectMapper.readValue(event.getPayload(), ProjectDeletedEvent.class);
                kafkaProducer.sendProjectDeletedEvent(deletedEvent);
                event.setProcessed(true);
                outboxEventRepository.save(event);
                log.info("Published outbox event [id={}, type={}]", event.getId(), event.getEventType());
            } catch (Exception e) {
                log.error("Failed to publish outbox event [id={}]: {}", event.getId(), e.getMessage());
            }
        }
    }
}