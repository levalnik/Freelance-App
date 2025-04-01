package org.levalnik.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.levalnik.DTO.events.UserCreatedEvent;
import org.levalnik.DTO.events.UserDeletedEvent;
import org.levalnik.config.KafkaConfig;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final BidService bidService;
    
    @KafkaListener(topics = KafkaConfig.USER_DELETED_TOPIC, groupId = "${spring.kafka.consumer.group-id}")
    public void handleUserDeleted(UserDeletedEvent event) {
        log.info("Received user deleted event: {}", event);
        
        if ("FREELANCER".equals(event.getUserType())) {
            bidService.cancelBidsByFreelancer(event.getUserId());
        }
    }
} 