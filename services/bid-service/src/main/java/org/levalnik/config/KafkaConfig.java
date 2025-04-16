package org.levalnik.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    public static final String BID_CREATED_TOPIC = "bid-created";
    public static final String BID_STATUS_UPDATED_TOPIC = "bid-status-updated";
    public static final String PROJECT_DELETED_TOPIC = "project-deleted";
    public static final String USER_DELETED_TOPIC = "user-deleted";

    @Bean
    public NewTopic bidCreatedTopic() {
        return TopicBuilder.name(BID_CREATED_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic bidStatusUpdatedTopic() {
        return TopicBuilder.name(BID_STATUS_UPDATED_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }
} 