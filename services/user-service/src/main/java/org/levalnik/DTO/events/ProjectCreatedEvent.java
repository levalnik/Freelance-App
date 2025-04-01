package org.levalnik.DTO.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectCreatedEvent {
    private UUID projectId;
    private String title;
    private String description;
    private Double budget;
    private UUID clientId;
    private String status;
    private LocalDateTime createdAt;
} 