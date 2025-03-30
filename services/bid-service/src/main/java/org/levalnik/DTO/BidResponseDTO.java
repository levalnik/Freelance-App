package org.levalnik.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.levalnik.model.enums.BidStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BidResponseDTO {
    private Long id;
    private Long projectId;
    private Long freelancerId;
    private BigDecimal amount;
    private BidStatus status;
    private LocalDateTime createdAt;
}