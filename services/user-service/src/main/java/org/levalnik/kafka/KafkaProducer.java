package org.levalnik.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.levalnik.DTO.events.UserCreatedEvent;
import org.levalnik.DTO.events.UserDeletedEvent;
import org.levalnik.DTO.events.UserUpdatedEvent;
import org.levalnik.config.KafkaConfig;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendUserCreatedEvent(UserCreatedEvent event) {
        log.info("Sending user created event: {}", event);
        kafkaTemplate.send(KafkaConfig.USER_CREATED_TOPIC, event.getUserId().toString(), event);
    }

    public void sendUserUpdatedEvent(UserUpdatedEvent event) {
        log.info("Sending user updated event: {}", event);
        kafkaTemplate.send(KafkaConfig.USER_UPDATED_TOPIC, event.getUserId().toString(), event);
    }

    public void sendUserDeletedEvent(UserDeletedEvent event) {
        log.info("Sending user deleted event: {}", event);
        kafkaTemplate.send(KafkaConfig.USER_DELETED_TOPIC, event.getUserId().toString(), event);
    }
} 