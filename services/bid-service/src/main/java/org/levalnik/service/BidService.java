package org.levalnik.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.levalnik.DTO.BidRequestDTO;
import org.levalnik.DTO.BidResponseDTO;
import org.levalnik.mapper.BidMapper;
import org.levalnik.model.Bid;
import org.levalnik.model.enums.BidStatus;
import org.levalnik.repository.BidRepository;
import org.levalnik.DTO.events.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BidService {

    private final BidRepository bidRepository;
    private final BidMapper bidMapper;
    private final KafkaProducerService kafkaProducerService;

    @Transactional
    public BidResponseDTO createBid(BidRequestDTO bidRequestDTO) {
        if (bidRequestDTO == null) {
            throw new IllegalArgumentException("BidRequestDTO cannot be null");
        }
        if (bidRequestDTO.getProjectId() == null || bidRequestDTO.getFreelancerId() == null) {
            throw new IllegalArgumentException("ProjectId and FreelancerId must not be null");
        }
        log.info("Creating new bid for project ID: {}, freelancer ID: {}", bidRequestDTO.getProjectId(), bidRequestDTO.getFreelancerId());
        Bid bid = bidMapper.toEntity(bidRequestDTO);
        Bid savedBid = bidRepository.save(bid);
        
        kafkaProducerService.sendBidCreatedEvent(BidCreatedEvent.builder()
                .bidId(UUID.fromString(savedBid.getId().toString()))
                .projectId(UUID.fromString(savedBid.getProjectId().toString()))
                .freelancerId(UUID.fromString(savedBid.getFreelancerId().toString()))
                .amount(savedBid.getAmount())
                .status(savedBid.getStatus())
                .createdAt(savedBid.getCreatedAt())
                .build());
                
        return bidMapper.toResponseDto(savedBid);
    }

    @Transactional(readOnly = true)
    public List<BidResponseDTO> getBidsByProject(UUID projectId) {
        log.info("Fetching bids for project ID: {}", projectId);
        List<Bid> bids = bidRepository.findByProjectId(projectId);

        return bids.stream()
                .map(bidMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public List<BidResponseDTO> getBidsByFreelancer(UUID freelancerId) {
        log.info("Fetching bids for freelancer ID: {}", freelancerId);
        List<Bid> bids = bidRepository.findByFreelancerId(freelancerId);

        return bids.stream()
                .map(bidMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public List<BidResponseDTO> getBidsByStatus(BidStatus status) {
        log.info("Fetching bids with status: {}", status);
        List<Bid> bids = bidRepository.findByStatus(status);

        return bids.stream()
                .map(bidMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public Optional<BidResponseDTO> updateBidStatus(UUID bidId, BidStatus newStatus) {
        log.info("Updating status of bid ID: {} to {}", bidId, newStatus);
        Optional<Bid> optionalBid = bidRepository.findById(bidId);

        if (optionalBid.isPresent()) {
            Bid bid = optionalBid.get();
            BidStatus oldStatus = bid.getStatus();
            bid.setStatus(newStatus);
            Bid updatedBid = bidRepository.save(bid);
            
            kafkaProducerService.sendBidStatusUpdatedEvent(BidStatusUpdatedEvent.builder()
                    .bidId(UUID.fromString(bidId.toString()))
                    .projectId(UUID.fromString(updatedBid.getProjectId().toString()))
                    .oldStatus(oldStatus)
                    .newStatus(newStatus)
                    .updatedAt(LocalDateTime.now())
                    .build());
                    
            return Optional.of(bidMapper.toResponseDto(updatedBid));
        } else {
            log.warn("Bid with ID: {} not found", bidId);
            return Optional.empty();
        }
    }

    @Transactional
    public void deleteBid(UUID bidId) {
        log.info("Deleting bid with ID: {}", bidId);
        if (!bidRepository.existsById(bidId)) {
            throw new EntityNotFoundException("Bid with ID: " + bidId + " not found");
        }
        bidRepository.deleteById(bidId);
    }

    @Transactional
    public void cancelBidsByFreelancer(UUID freelancerId) {
        log.info("Cancelling all bids for freelancer: {}", freelancerId);
        List<Bid> bids = bidRepository.findByFreelancerId(freelancerId);
        
        for (Bid bid : bids) {
            bid.setStatus(BidStatus.CANCELLED);
            Bid savedBid = bidRepository.save(bid);
            
            // Отправляем событие об изменении статуса ставки
            kafkaProducerService.sendBidStatusUpdatedEvent(
                BidStatusUpdatedEvent.builder()
                    .bidId(savedBid.getId())
                    .projectId(savedBid.getProjectId())
                    .oldStatus(BidStatus.PENDING)
                    .newStatus(BidStatus.CANCELLED)
                    .updatedAt(LocalDateTime.now())
                    .build()
            );
        }
        
        log.info("Cancelled {} bids for freelancer: {}", bids.size(), freelancerId);
    }
}