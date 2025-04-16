package org.levalnik.kafkaEvent.bidKafkaEvent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.levalnik.enums.bidEnum.BidStatus;
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