package org.levalnik.dto.bidDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.levalnik.enums.bidEnum.BidStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BidRequestDTO {

    @NotNull(message = "Project ID must not be null")
    private UUID projectId;

    @NotNull(message = "Freelancer ID must not be null")
    private UUID freelancerId;

    @NotNull(message = "Bid amount must not be null")
    @DecimalMin(value = "0.01", message = "Bid amount must be greater than zero")
    private BigDecimal amount;

    @NotNull(message = "Bid status must not be null")
    private BidStatus status;

    private LocalDateTime createdAt = LocalDateTime.now();

    @Size(max = 500, message = "Comment length must not exceed 500 characters")
    private String comment;
}