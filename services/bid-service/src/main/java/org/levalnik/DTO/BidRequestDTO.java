package org.levalnik.DTO;

import lombok.Data;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class BidRequestDTO {

    @NotNull(message = "Project ID must not be null")
    private Long projectId;

    @NotNull(message = "Freelancer ID must not be null")
    private Long freelancerId;

    @NotNull(message = "Bid amount must not be null")
    @DecimalMin(value = "0.01", message = "Bid amount must be greater than zero")
    private BigDecimal amount;
}