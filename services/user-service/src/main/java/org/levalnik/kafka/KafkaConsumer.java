package org.levalnik.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.levalnik.kafkaEvent.bidKafkaEvent.BidCreatedEvent;
import org.levalnik.kafkaEvent.projectKafkaEvent.ProjectCreatedEvent;
import org.levalnik.config.KafkaConfig;
import org.levalnik.exception.EntityNotFoundException;
import org.levalnik.service.UserService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaConsumer {

    private final UserService userService;

    @KafkaListener(topics = KafkaConfig.PROJECT_CREATED_TOPIC, groupId = "${spring.kafka.consumer.group-id}")
    @Transactional
    public void handleProjectCreated(ProjectCreatedEvent event,
                                     @Header(KafkaHeaders.RECEIVED_PARTITION) Integer partition,
                                     @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        try {
            log.info("Received project created event from topic: {}, partition: {}, event: {}",
                    topic, partition, event);

            userService.updateProjectCount(event.getClientId());
            log.info("Successfully processed project created event for client: {}", event.getClientId());
        } catch (EntityNotFoundException e) {
            log.error("Failed to process project created event: Client not found with ID: {}",
                    event.getClientId(), e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while processing project created event: {}", e.getMessage(), e);
            throw e;
        }
    }

    @KafkaListener(topics = KafkaConfig.BID_CREATED_TOPIC, groupId = "${spring.kafka.consumer.group-id}")
    @Transactional
    public void handleBidCreated(BidCreatedEvent event,
                                 @Header(KafkaHeaders.RECEIVED_PARTITION) Integer partition,
                                 @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        try {
            log.info("Received bid created event from topic: {}, partition: {}, event: {}",
                    topic, partition, event);

            userService.updateBidCount(event.getFreelancerId());
            log.info("Successfully processed bid created event for freelancer: {}",
                    event.getFreelancerId());
        } catch (EntityNotFoundException e) {
            log.error("Failed to process bid created event: Freelancer not found with ID: {}",
                    event.getFreelancerId(), e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while processing bid created event: {}", e.getMessage(), e);
            throw e;
        }
    }
} 