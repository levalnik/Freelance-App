package org.levalnik.repository;

import org.levalnik.model.Bid;
import org.levalnik.model.enums.BidStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface BidRepository extends JpaRepository<Bid, UUID> {

    List<Bid> findByProjectId(UUID projectId);

    List<Bid> findByFreelancerId(UUID freelancerId);

    List<Bid> findByStatus(BidStatus status);

    List<Bid> findByFreelancerIdAndStatus(UUID freelancerId, BidStatus status);
}