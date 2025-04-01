package org.levalnik.mapper;

import org.levalnik.DTO.BidRequestDTO;
import org.levalnik.DTO.BidResponseDTO;
import org.levalnik.model.Bid;
import org.levalnik.model.enums.BidStatus;
import org.springframework.stereotype.Component;

@Component
public class BidMapper {

    public Bid toEntity(BidRequestDTO dto) {
        Bid bid = new Bid();
        bid.setProjectId(dto.getProjectId());
        bid.setFreelancerId(dto.getFreelancerId());
        bid.setAmount(dto.getAmount());
        bid.setStatus(BidStatus.PENDING);
        bid.setCreatedAt(java.time.LocalDateTime.now());
        return bid;
    }

    public BidResponseDTO toResponseDto(Bid bid) {
        return BidResponseDTO.builder()
                .id(bid.getId())
                .projectId(bid.getProjectId())
                .freelancerId(bid.getFreelancerId())
                .amount(bid.getAmount())
                .status(bid.getStatus())
                .createdAt(bid.getCreatedAt())
                .build();
    }
}