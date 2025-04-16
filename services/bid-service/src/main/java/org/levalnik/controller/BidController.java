package org.levalnik.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.levalnik.DTO.BidRequestDTO;
import org.levalnik.DTO.BidResponseDTO;
import org.levalnik.enums.bidEnum.BidStatus;
import org.levalnik.service.BidService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/bids")
@RequiredArgsConstructor
public class BidController {

    private final BidService bidService;

    @PostMapping
    public ResponseEntity<BidResponseDTO> createBid(@Valid @RequestBody BidRequestDTO bidRequestDTO) {
        log.info("Creating new bid for project ID: {}, freelancer ID: {}",
                bidRequestDTO.getProjectId(), bidRequestDTO.getFreelancerId());
        return ResponseEntity.ok(bidService.createBid(bidRequestDTO));
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<BidResponseDTO>> getBidsByProject(@PathVariable UUID projectId) {
        log.info("Fetching bids for project ID: {}", projectId);
        return ResponseEntity.ok(bidService.getBidsByProject(projectId));
    }

    @GetMapping("/freelancer/{freelancerId}")
    public ResponseEntity<List<BidResponseDTO>> getBidsByFreelancer(@PathVariable UUID freelancerId) {
        log.info("Fetching bids for freelancer ID: {}", freelancerId);
        return ResponseEntity.ok(bidService.getBidsByFreelancer(freelancerId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<BidResponseDTO>> getBidsByStatus(@PathVariable BidStatus status) {
        log.info("Fetching bids with status: {}", status);
        return ResponseEntity.ok(bidService.getBidsByStatus(status));
    }

    @PutMapping("/{bidId}/status")
    public ResponseEntity<BidResponseDTO> updateBidStatus(
            @PathVariable UUID bidId,
            @RequestParam BidStatus status) {
        log.info("Updating status of bid ID: {} to {}", bidId, status);
        return ResponseEntity.ok(bidService.updateBidStatus(bidId, status));
    }

    @DeleteMapping("/{bidId}")
    public ResponseEntity<Void> deleteBid(@PathVariable UUID bidId) {
        log.info("Deleting bid with ID: {}", bidId);
        bidService.deleteBid(bidId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/freelancer/{freelancerId}/cancel")
    public ResponseEntity<Void> cancelBidsByFreelancer(@PathVariable UUID freelancerId) {
        log.info("Cancelling all bids for freelancer: {}", freelancerId);
        bidService.cancelBidsByFreelancer(freelancerId);
        return ResponseEntity.ok().build();
    }
}