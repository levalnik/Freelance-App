package org.levalnik.service;

import org.levalnik.model.Bid;
import org.levalnik.model.enums.BidStatus;
import org.levalnik.repository.BidRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BidService {

    private final BidRepository bidRepository;

    @Transactional(readOnly = true)
    public List<Bid> getBidsByProject(Long projectId) {
        log.info("Fetching bids for project ID: {}", projectId);
        return bidRepository.findByProjectId(projectId);
    }

    @Transactional(readOnly = true)
    public List<Bid> getBidsByFreelancer(Long freelancerId) {
        log.info("Fetching bids for freelancer ID: {}", freelancerId);
        return bidRepository.findByFreelancerId(freelancerId);
    }

    @Transactional(readOnly = true)
    public List<Bid> getBidsByStatus(BidStatus status) {
        log.info("Fetching bids with status: {}", status);
        return bidRepository.findByStatus(status);
    }

    @Transactional
    public Bid createBid(Bid bid) {
        bid.setStatus(BidStatus.Pending);
        log.info("Creating new bid for project ID: {} by freelancer ID: {}", bid.getProjectId(), bid.getFreelancerId());
        return bidRepository.save(bid);
    }

    @Transactional
    public Optional<Bid> updateBidStatus(Long bidId, BidStatus status) {
        return bidRepository.findById(bidId).map(bid -> {
            log.info("Updating status of bid ID: {} to {}", bidId, status);
            bid.setStatus(status);
            return bidRepository.save(bid);
        });
    }

    @Transactional
    public void deleteBid(Long bidId) {
        if (bidRepository.existsById(bidId)) {
            log.info("Deleting bid with ID: {}", bidId);
            bidRepository.deleteById(bidId);
        } else {
            log.warn("Bid with ID: {} not found", bidId);
        }
    }
}