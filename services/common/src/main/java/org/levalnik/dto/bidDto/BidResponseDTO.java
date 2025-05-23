package org.levalnik.dto.bidDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.levalnik.enums.bidEnum.BidStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BidResponseDTO {
    private UUID id;
    private UUID projectId;
    private UUID freelancerId;
    private BigDecimal amount;
    private BidStatus status;
    private LocalDateTime createdAt;
}