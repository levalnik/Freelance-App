package org.levalnik.kafkaEvent.projectKafkaEvent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.levalnik.enums.projectEnum.ProjectStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectCreatedEvent {
    private UUID projectId;
    private String title;
    private String description;
    private Double budget;
    private UUID clientId;
    private ProjectStatus status;
    private LocalDateTime createdAt;
} 