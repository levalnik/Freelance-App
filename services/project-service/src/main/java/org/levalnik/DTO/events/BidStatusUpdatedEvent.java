package org.levalnik.DTO.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BidStatusUpdatedEvent {
    private UUID bidId;
    private UUID projectId;
    private String oldStatus;
    private String newStatus;
    private LocalDateTime updatedAt;
} 