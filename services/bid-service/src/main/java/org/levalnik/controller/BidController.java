package org.levalnik.controller;

import org.levalnik.model.Bid;
import org.levalnik.model.enums.BidStatus;
import org.levalnik.service.BidService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/bids")
@RequiredArgsConstructor
public class BidController {

    private final BidService bidService;

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<Bid>> getBidsByProject(@PathVariable Long projectId) {
        log.info("Fetching bids for project ID: {}", projectId);
        return ResponseEntity.ok(bidService.getBidsByProject(projectId));
    }

    @GetMapping("/freelancer/{freelancerId}")
    public ResponseEntity<List<Bid>> getBidsByFreelancer(@PathVariable Long freelancerId) {
        log.info("Fetching bids for freelancer ID: {}", freelancerId);
        return ResponseEntity.ok(bidService.getBidsByFreelancer(freelancerId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Bid>> getBidsByStatus(@PathVariable BidStatus status) {
        log.info("Fetching bids with status: {}", status);
        return ResponseEntity.ok(bidService.getBidsByStatus(status));
    }

    @PostMapping
    public ResponseEntity<Bid> createBid(@RequestBody Bid bid) {
        log.info("Creating bid for project ID: {}, freelancer ID: {}", bid.getProjectId(), bid.getFreelancerId());
        return ResponseEntity.ok(bidService.createBid(bid));
    }

    @PutMapping("/{bidId}/status/{status}")
    public ResponseEntity<Bid> updateBidStatus(@PathVariable Long bidId, @PathVariable BidStatus status) {
        Optional<Bid> updatedBid = bidService.updateBidStatus(bidId, status);
        return updatedBid.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{bidId}")
    public ResponseEntity<Void> deleteBid(@PathVariable Long bidId) {
        log.info("Deleting bid with ID: {}", bidId);
        bidService.deleteBid(bidId);
        return ResponseEntity.noContent().build();
    }
}