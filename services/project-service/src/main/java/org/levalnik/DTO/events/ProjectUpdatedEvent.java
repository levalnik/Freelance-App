package org.levalnik.DTO.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.levalnik.model.enums.Status;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectUpdatedEvent {
    private UUID projectId;
    private String title;
    private String description;
    private Double budget;
    private Status status;
    private LocalDateTime updatedAt;
} 