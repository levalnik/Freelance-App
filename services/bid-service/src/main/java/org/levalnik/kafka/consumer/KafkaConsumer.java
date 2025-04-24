package org.levalnik.kafka.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.levalnik.kafka.config.KafkaConfig;
import org.levalnik.enums.userEnum.UserRole;
import org.levalnik.exception.EntityNotFoundException;
import org.levalnik.bid.service.BidService;
import org.levalnik.kafkaEvent.userKafkaEvent.*;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaConsumer {

    private final BidService bidService;

    @KafkaListener(topics = KafkaConfig.USER_DELETED_TOPIC, groupId = "${spring.kafka.consumer.group-id}")
    @Transactional
    public void handleUserDeleted(UserDeletedEvent event,
                                  @Header(KafkaHeaders.RECEIVED_PARTITION) Integer partition,
                                  @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        try {
            log.info("Received user deleted event from topic: {}, partition: {}, event: {}",
                    topic, partition, event);

            if (UserRole.FREELANCER.equals(event.getUserRole())) {
                bidService.cancelBidsByFreelancer(event.getUserId());
                log.info("Successfully cancelled all bids for freelancer: {}", event.getUserId());
            }
        } catch (EntityNotFoundException e) {
            log.error("Failed to process user deleted event: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while processing user deleted event: {}", e.getMessage(), e);
            throw e;
        }
    }

    @KafkaListener(topics = KafkaConfig.PROJECT_DELETED_TOPIC, groupId = "${spring.kafka.consumer.group-id}")
    @Transactional
    public void handleProjectDeleted(UUID projectId,
                                     @Header(KafkaHeaders.RECEIVED_PARTITION) Integer partition,
                                     @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        try {
            log.info("Received project deleted event from topic: {}, partition: {}, projectId: {}",
                    topic, partition, projectId);

            bidService.cancelBidsByProject(projectId);
            log.info("Successfully cancelled all bids for project: {}", projectId);
        } catch (EntityNotFoundException e) {
            log.error("Failed to process project deleted event: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while processing project deleted event: {}", e.getMessage(), e);
            throw e;
        }
    }
} 