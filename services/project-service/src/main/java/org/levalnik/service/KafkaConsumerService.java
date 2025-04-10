package org.levalnik.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.levalnik.DTO.events.BidCreatedEvent;
import org.levalnik.DTO.events.BidStatusUpdatedEvent;
import org.levalnik.DTO.events.ProjectUpdatedEvent;
import org.levalnik.DTO.events.UserDeletedEvent;
import org.levalnik.config.KafkaConfig;
import org.levalnik.exception.EntityNotFoundException;
import org.levalnik.model.enums.Status;
import org.levalnik.repository.ProjectRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(value = "spring.kafka.bootstrap-servers")
public class KafkaConsumerService {

    private final ProjectRepository projectRepository;
    private final KafkaProducerService kafkaProducerService;
    private final ProjectService projectService;
    
    @KafkaListener(topics = KafkaConfig.BID_CREATED_TOPIC, groupId = "${spring.kafka.consumer.group-id}")
    @Transactional
    public void handleBidCreated(BidCreatedEvent event,
                               @Header(KafkaHeaders.RECEIVED_PARTITION) Integer partition,
                               @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        try {
            log.info("Received bid created event from topic: {}, partition: {}, event: {}", 
                    topic, partition, event);
            
            projectRepository.findById(event.getProjectId())
                .ifPresentOrElse(
                    project -> {
                        projectService.updateBidCount(project.getId());
                        log.info("Successfully updated bid count for project: {}", project.getId());
                    },
                    () -> {
                        log.error("Project not found for bid created event: {}", event.getProjectId());
                        throw new EntityNotFoundException("Project not found with ID: " + event.getProjectId());
                    }
                );
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
            
            if ("ACCEPTED".equals(event.getNewStatus())) {
                projectRepository.findById(event.getProjectId())
                    .ifPresentOrElse(
                        project -> {
                            log.info("Updating project status to IN_PROGRESS for project: {}", project.getId());
                            project.setStatus(Status.IN_PROGRESS);
                            projectRepository.save(project);
                            
                            kafkaProducerService.sendProjectUpdatedEvent(
                                ProjectUpdatedEvent.builder()
                                    .projectId(project.getId())
                                    .title(project.getTitle())
                                    .description(project.getDescription())
                                    .budget(project.getBudget())
                                    .status(project.getStatus())
                                    .updatedAt(LocalDateTime.now())
                                    .build()
                            );
                            log.info("Successfully updated project status and sent update event for project: {}", 
                                    project.getId());
                        },
                        () -> {
                            log.error("Project not found for bid status update event: {}", event.getProjectId());
                            throw new EntityNotFoundException("Project not found with ID: " + event.getProjectId());
                        }
                    );
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
            
            if ("CLIENT".equals(event.getUserType())) {
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