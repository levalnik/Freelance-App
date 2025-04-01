package org.levalnik.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.levalnik.DTO.events.BidCreatedEvent;
import org.levalnik.DTO.events.ProjectCreatedEvent;
import org.levalnik.config.KafkaConfig;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final UserService userService;
    
    @KafkaListener(topics = KafkaConfig.PROJECT_CREATED_TOPIC, groupId = "${spring.kafka.consumer.group-id}")
    public void handleProjectCreated(ProjectCreatedEvent event) {
        log.info("Received project created event: {}", event);
        
        userService.updateProjectCount(event.getClientId());
    }
    
    @KafkaListener(topics = KafkaConfig.BID_CREATED_TOPIC, groupId = "${spring.kafka.consumer.group-id}")
    public void handleBidCreated(BidCreatedEvent event) {
        log.info("Received bid created event: {}", event);
        
        userService.updateBidCount(event.getFreelancerId());
    }
} 