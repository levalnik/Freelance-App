package org.levalnik.repository;

import org.levalnik.model.Bid;
import org.levalnik.model.enums.BidStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BidRepository extends JpaRepository<Bid, Long> {

    List<Bid> findByProjectId(Long projectId);

    List<Bid> findByFreelancerId(Long freelancerId);

    List<Bid> findByStatus(BidStatus status);
}