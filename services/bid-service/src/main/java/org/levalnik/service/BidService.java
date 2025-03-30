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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service for managing bids in the system.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BidService {

    private final BidRepository bidRepository;
    private final BidMapper bidMapper;

    /**
     * Creates a new bid in the system.
     *
     * @param bidRequestDTO the bid request containing bid details
     * @return the created bid response
     * @throws IllegalArgumentException if the request is invalid
     */
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
        return bidMapper.toResponseDto(savedBid);
    }

    @Transactional(readOnly = true)
    public List<BidResponseDTO> getBidsByProject(Long projectId) {
        log.info("Fetching bids for project ID: {}", projectId);
        List<Bid> bids = bidRepository.findByProjectId(projectId);

        return bids.stream()
                .map(bidMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public List<BidResponseDTO> getBidsByFreelancer(Long freelancerId) {
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
    public Optional<BidResponseDTO> updateBidStatus(Long bidId, BidStatus status) {
        log.info("Updating status of bid ID: {} to {}", bidId, status);
        Optional<Bid> optionalBid = bidRepository.findById(bidId);

        if (optionalBid.isPresent()) {
            Bid bid = optionalBid.get();
            bid.setStatus(status);
            Bid updatedBid = bidRepository.save(bid);
            return Optional.of(bidMapper.toResponseDto(updatedBid));
        } else {
            log.warn("Bid with ID: {} not found", bidId);
            return Optional.empty();
        }
    }

    @Transactional
    public void deleteBid(Long bidId) {
        log.info("Deleting bid with ID: {}", bidId);
        if (!bidRepository.existsById(bidId)) {
            throw new EntityNotFoundException("Bid with ID: " + bidId + " not found");
        }
        bidRepository.deleteById(bidId);
    }
}