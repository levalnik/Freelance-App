package org.levalnik.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.levalnik.DTO.events.BidCreatedEvent;
import org.levalnik.DTO.events.BidStatusUpdatedEvent;
import org.levalnik.DTO.events.ProjectUpdatedEvent;
import org.levalnik.DTO.events.UserDeletedEvent;
import org.levalnik.config.KafkaConfig;
import org.levalnik.model.enums.Status;
import org.levalnik.repository.ProjectRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
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
    public void handleBidCreated(BidCreatedEvent event) {
        log.info("Received bid created event: {}", event);
        
        projectRepository.findById(event.getProjectId())
            .ifPresent(project -> {
                log.info("Updating bid count for project: {}", project.getId());
            });
    }
    
    @KafkaListener(topics = KafkaConfig.BID_STATUS_UPDATED_TOPIC, groupId = "${spring.kafka.consumer.group-id}")
    public void handleBidStatusUpdated(BidStatusUpdatedEvent event) {
        log.info("Received bid status updated event: {}", event);
        
        if ("ACCEPTED".equals(event.getNewStatus())) {
            projectRepository.findById(event.getProjectId())
                .ifPresent(project -> {
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
                });
        }
    }

    @KafkaListener(topics = KafkaConfig.USER_DELETED_TOPIC, groupId = "${spring.kafka.consumer.group-id}")
    public void handleUserDeleted(UserDeletedEvent event) {
        log.info("Received user deleted event: {}", event);
        if ("CLIENT".equals(event.getUserType())) {
            projectService.closeProjectsByClient(event.getUserId());
        }
    }
} 