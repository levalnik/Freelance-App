package org.levalnik.dto.bid;

import lombok.Data;
import org.levalnik.enums.bidEnum.BidStatus;

import java.util.UUID;

@Data
public class BidResponse {
    private UUID id;
    private UUID projectId;
    private UUID userId;
    private String comment;
    private BidStatus status;
}
