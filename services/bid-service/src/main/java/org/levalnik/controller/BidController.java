package org.levalnik.controller;

import org.levalnik.DTO.BidRequestDTO;
import org.levalnik.DTO.BidResponseDTO;
import org.levalnik.model.enums.BidStatus;
import org.levalnik.service.BidService;
import org.levalnik.mapper.BidMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/bids")
@RequiredArgsConstructor
public class BidController {

    private final BidService bidService;

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<BidResponseDTO>> getBidsByProject(@PathVariable Long projectId) {
        return ResponseEntity.ok(bidService.getBidsByProject(projectId));
    }

    @GetMapping("/freelancer/{freelancerId}")
    public ResponseEntity<List<BidResponseDTO>> getBidsByFreelancer(@PathVariable Long freelancerId) {
        return ResponseEntity.ok(bidService.getBidsByFreelancer(freelancerId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<BidResponseDTO>> getBidsByStatus(@PathVariable BidStatus status) {
        return ResponseEntity.ok(bidService.getBidsByStatus(status));
    }

    @PostMapping
    public ResponseEntity<BidResponseDTO> createBid(@RequestBody BidRequestDTO bidRequestDTO) {
        return ResponseEntity.ok(bidService.createBid(bidRequestDTO));
    }

    @PutMapping("/{bidId}/status")
    public ResponseEntity<BidResponseDTO> updateBidStatus(
            @PathVariable Long bidId,
            @RequestParam BidStatus status) {
        return bidService.updateBidStatus(bidId, status)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{bidId}")
    public ResponseEntity<Void> deleteBid(@PathVariable Long bidId) {
        bidService.deleteBid(bidId);
        return ResponseEntity.noContent().build();
    }
}