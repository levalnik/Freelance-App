package org.levalnik.DTO.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BidCreatedEvent {
    private UUID bidId;
    private UUID projectId;
    private UUID freelancerId;
    private BigDecimal amount;
    private LocalDateTime createdAt;
} 