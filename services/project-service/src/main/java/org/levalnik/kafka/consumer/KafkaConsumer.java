package org.levalnik.kafka.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.levalnik.enums.bidEnum.BidStatus;
import org.levalnik.enums.userEnum.UserRole;
import org.levalnik.kafkaEvent.bidKafkaEvent.*;
import org.levalnik.kafkaEvent.userKafkaEvent.*;
import org.levalnik.kafka.config.KafkaConfig;
import org.levalnik.exception.EntityNotFoundException;
import org.levalnik.enums.projectEnum.ProjectStatus;
import org.levalnik.project.service.ProjectService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(value = "spring.kafka.bootstrap-servers")
public class KafkaConsumer {

    private final ProjectService projectService;

    @KafkaListener(topics = KafkaConfig.BID_CREATED_TOPIC, groupId = "${spring.kafka.consumer.group-id}")
    @Transactional
    public void handleBidCreated(BidCreatedEvent event,
                                 @Header(KafkaHeaders.RECEIVED_PARTITION) Integer partition,
                                 @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        try {
            log.info("Received bid created event from topic: {}, partition: {}, event: {}",
                    topic, partition, event);

            projectService.updateBidCount(event.getProjectId());
            log.info("Successfully updated bid count for project: {}", event.getProjectId());

        } catch (EntityNotFoundException e) {
            log.error("Failed to process bid created event: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while processing bid created event: {}", e.getMessage(), e);
            throw e;
        }
    }

    @KafkaListener(topics = KafkaConfig.BID_STATUS_UPDATED_TOPIC, groupId = "${spring.kafka.consumer.group-id}")
    @Transactional
    public void handleBidStatusUpdated(BidStatusUpdatedEvent event,
                                       @Header(KafkaHeaders.RECEIVED_PARTITION) Integer partition,
                                       @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        try {
            log.info("Received bid status updated event from topic: {}, partition: {}, event: {}",
                    topic, partition, event);

            if (BidStatus.ACCEPTED.equals(event.getNewStatus())) {
                log.info("Updating project status to IN_PROGRESS for project: {}", event.getProjectId());
                projectService.updateStatus(event.getProjectId(), ProjectStatus.IN_PROGRESS);
                log.info("Successfully updated project status and sent update event for project: {}",
                        event.getProjectId());
            }
        } catch (EntityNotFoundException e) {
            log.error("Failed to process bid status updated event: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while processing bid status updated event: {}", e.getMessage(), e);
            throw e;
        }
    }

    @KafkaListener(topics = KafkaConfig.USER_DELETED_TOPIC, groupId = "${spring.kafka.consumer.group-id}")
    @Transactional
    public void handleUserDeleted(UserDeletedEvent event,
                                  @Header(KafkaHeaders.RECEIVED_PARTITION) Integer partition,
                                  @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        try {
            log.info("Received user deleted event from topic: {}, partition: {}, event: {}",
                    topic, partition, event);

            if (UserRole.Client.equals(event.getUserRole())) {
                projectService.closeProjectsByClient(event.getUserId());
                log.info("Successfully closed all projects for client: {}", event.getUserId());
            }
        } catch (EntityNotFoundException e) {
            log.error("Failed to process user deleted event: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while processing user deleted event: {}", e.getMessage(), e);
            throw e;
        }
    }
} 