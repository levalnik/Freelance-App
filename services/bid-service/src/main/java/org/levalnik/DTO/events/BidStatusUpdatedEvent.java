package org.levalnik.DTO.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.levalnik.model.enums.BidStatus;
import java.util.UUID;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BidStatusUpdatedEvent {
    private UUID bidId;
    private UUID projectId;
    private UUID freelancerId;
    private BidStatus oldStatus;
    private BidStatus newStatus;
    private LocalDateTime updatedAt;
    private String reason;
} 