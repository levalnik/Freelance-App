package org.levalnik.kafkaEvent.projectKafkaEvent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDeletedEvent {
    private UUID projectId;
    private LocalDateTime deletedAt;
} 