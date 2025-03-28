package org.levalnik.DTO;

import lombok.Data;
import org.levalnik.model.enums.BidStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BidResponseDTO {
    private Long id;
    private Long projectId;
    private String projectName;
    private Long freelancerId;
    private String freelancerName;
    private BigDecimal amount;
    private LocalDateTime createdAt;
    private BidStatus status;
}