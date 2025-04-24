package org.levalnik.bid.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.levalnik.bid.DTO.BidRequestDTO;
import org.levalnik.bid.DTO.BidResponseDTO;
import org.levalnik.kafka.producer.KafkaProducer;
import org.levalnik.kafkaEvent.bidKafkaEvent.*;
import org.levalnik.bid.mapper.BidMapper;
import org.levalnik.bid.model.Bid;
import org.levalnik.enums.bidEnum.BidStatus;
import org.levalnik.bid.repository.BidRepository;
import org.levalnik.outbox.model.OutboxEvent;
import org.levalnik.outbox.repository.OutboxEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class BidService {

    private final BidRepository bidRepository;
    private final BidMapper bidMapper;
    private final ObjectMapper objectMapper;
    private final OutboxEventRepository outboxEventRepository;

    @Transactional
    public BidResponseDTO createBid(BidRequestDTO bidRequestDTO) {
        log.info("Creating new bid for project ID: {}, freelancer ID: {}", bidRequestDTO.getProjectId(), bidRequestDTO.getFreelancerId());

        Bid bid = bidMapper.toEntity(bidRequestDTO);
        bid.setCreatedAt(LocalDateTime.now());
        bid.setUpdatedAt(LocalDateTime.now());
        bid.setStatus(BidStatus.PENDING);
        Bid savedBid = bidRepository.save(bid);

        BidCreatedEvent eventPayload = BidCreatedEvent.builder()
                .bidId(savedBid.getId())
                .projectId(savedBid.getProjectId())
                .freelancerId(savedBid.getFreelancerId())
                .amount(savedBid.getAmount())
                .status(savedBid.getStatus())
                .createdAt(savedBid.getCreatedAt())
                .build();

        try {
            String payload = objectMapper.writeValueAsString(eventPayload);

            OutboxEvent event = OutboxEvent.builder()
                    .aggregateType("Bid")
                    .aggregateId(savedBid.getId().toString())
                    .eventType("BidCreatedEvent")
                    .payload(payload)
                    .createdAt(LocalDateTime.now())
                    .build();

            outboxEventRepository.save(event);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize BidCreatedEvent for bid ID: {}", savedBid.getId(), e);
            throw new RuntimeException("Failed to serialize event payload", e);
        }

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

    @Transactional(readOnly = true)
    public List<BidResponseDTO> getBidsByFreelancer(UUID freelancerId) {
        log.info("Fetching bids for freelancer ID: {}", freelancerId);
        List<Bid> bids = bidRepository.findByFreelancerId(freelancerId);
        return bids.stream()
                .map(bidMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BidResponseDTO> getBidsByStatus(BidStatus status) {
        log.info("Fetching bids with status: {}", status);
        List<Bid> bids = bidRepository.findByStatus(status);
        return bids.stream()
                .map(bidMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public BidResponseDTO updateBidStatus(UUID bidId, BidStatus status) {
        log.info("Updating status of bid ID: {} to {}", bidId, status);
        Bid bid = bidRepository.findById(bidId)
                .orElseThrow(() -> new EntityNotFoundException("Bid not found with ID: " + bidId));

        BidStatus oldStatus = bid.getStatus();
        bid.setStatus(status);
        bid.setUpdatedAt(LocalDateTime.now());
        Bid updatedBid = bidRepository.save(bid);

        BidStatusUpdatedEvent eventPayload = BidStatusUpdatedEvent.builder()
                .bidId(updatedBid.getId())
                .projectId(updatedBid.getProjectId())
                .freelancerId(updatedBid.getFreelancerId())
                .oldStatus(oldStatus)
                .newStatus(status)
                .updatedAt(updatedBid.getUpdatedAt())
                .build();

        try {
            String payload = objectMapper.writeValueAsString(eventPayload);

            OutboxEvent event = OutboxEvent.builder()
                    .aggregateType("Bid")
                    .aggregateId(updatedBid.getId().toString())
                    .eventType("BidStatusUpdatedEvent")
                    .payload(payload)
                    .createdAt(LocalDateTime.now())
                    .build();

            outboxEventRepository.save(event);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize BidStatusUpdatedEvent for bid ID: {}", updatedBid.getId(), e);
            throw new RuntimeException("Failed to serialize event payload", e);
        }

        return bidMapper.toResponseDto(updatedBid);
    }

    @Transactional
    public void deleteBid(UUID bidId) {
        log.info("Deleting bid with ID: {}", bidId);
        if (!bidRepository.existsById(bidId)) {
            throw new EntityNotFoundException("Bid not found with ID: " + bidId);
        }
        bidRepository.deleteById(bidId);
        log.info("Bid with ID {} successfully deleted", bidId);
    }

    @Transactional
    public void cancelBidsByFreelancer(UUID freelancerId) {
        log.info("Cancelling all bids for freelancer: {}", freelancerId);
        List<Bid> bids = bidRepository.findByFreelancerIdAndStatus(freelancerId, BidStatus.PENDING);

        for (Bid bid : bids) {
            bid.setStatus(BidStatus.CANCELLED);
            bid.setUpdatedAt(LocalDateTime.now());
            Bid updatedBid = bidRepository.save(bid);

            BidStatusUpdatedEvent eventPayload = BidStatusUpdatedEvent.builder()
                    .bidId(updatedBid.getId())
                    .projectId(updatedBid.getProjectId())
                    .freelancerId(updatedBid.getFreelancerId())
                    .oldStatus(BidStatus.PENDING)
                    .newStatus(BidStatus.CANCELLED)
                    .updatedAt(updatedBid.getUpdatedAt())
                    .reason("Freelancer account deleted")
                    .build();

            try {
                String payload = objectMapper.writeValueAsString(eventPayload);

                OutboxEvent event = OutboxEvent.builder()
                        .aggregateType("Bid")
                        .aggregateId(updatedBid.getId().toString())
                        .eventType("BidStatusUpdatedEvent")
                        .payload(payload)
                        .createdAt(LocalDateTime.now())
                        .build();

                outboxEventRepository.save(event);
            } catch (JsonProcessingException e) {
                log.error("Failed to serialize BidStatusUpdatedEvent for bid ID: {}", updatedBid.getId(), e);
                throw new RuntimeException("Failed to serialize event payload", e);
            }
        }

        log.info("Cancelled {} bids for freelancer: {}", bids.size(), freelancerId);
    }

    @Transactional
    public void cancelBidsByProject(UUID projectId) {
        log.info("Cancelling all bids for project ID: {}", projectId);
        List<Bid> bids = bidRepository.findByProjectId(projectId);

        for (Bid bid : bids) {
            bid.setStatus(BidStatus.CANCELLED);
            bid.setUpdatedAt(LocalDateTime.now());
            Bid updatedBid = bidRepository.save(bid);

            BidStatusUpdatedEvent eventPayload = BidStatusUpdatedEvent.builder()
                    .bidId(updatedBid.getId())
                    .projectId(updatedBid.getProjectId())
                    .freelancerId(updatedBid.getFreelancerId())
                    .oldStatus(BidStatus.PENDING)
                    .newStatus(BidStatus.CANCELLED)
                    .updatedAt(updatedBid.getUpdatedAt())
                    .reason("Project deleted")
                    .build();

            try {
                String payload = objectMapper.writeValueAsString(eventPayload);

                OutboxEvent event = OutboxEvent.builder()
                        .aggregateType("Bid")
                        .aggregateId(updatedBid.getId().toString())
                        .eventType("BidStatusUpdatedEvent")
                        .payload(payload)
                        .createdAt(LocalDateTime.now())
                        .build();

                outboxEventRepository.save(event);
            } catch (JsonProcessingException e) {
                log.error("Failed to serialize BidStatusUpdatedEvent for bid ID: {}", updatedBid.getId(), e);
                throw new RuntimeException("Failed to serialize event payload", e);
            }
        }

        log.info("Cancelled {} bids for project: {}", bids.size(), projectId);
    }
}