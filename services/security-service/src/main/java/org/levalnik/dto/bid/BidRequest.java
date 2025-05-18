package org.levalnik.dto.bid;

import lombok.Data;

import java.util.UUID;

@Data
public class BidRequest {
    private UUID projectId;
    private UUID userId;
    private String comment;
}
