package org.levalnik.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.levalnik.DTO.events.ProjectCreatedEvent;
import org.levalnik.DTO.events.ProjectDeletedEvent;
import org.levalnik.DTO.events.ProjectUpdatedEvent;
import org.levalnik.config.KafkaConfig;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendProjectCreatedEvent(ProjectCreatedEvent event) {
        log.info("Sending project created event: {}", event);
        kafkaTemplate.send(KafkaConfig.PROJECT_CREATED_TOPIC, event.getProjectId().toString(), event);
    }

    public void sendProjectUpdatedEvent(ProjectUpdatedEvent event) {
        log.info("Sending project updated event: {}", event);
        kafkaTemplate.send(KafkaConfig.PROJECT_UPDATED_TOPIC, event.getProjectId().toString(), event);
    }

    public void sendProjectDeletedEvent(ProjectDeletedEvent event) {
        log.info("Sending project deleted event: {}", event);
        kafkaTemplate.send(KafkaConfig.PROJECT_DELETED_TOPIC, event.getProjectId().toString(), event);
    }
} 