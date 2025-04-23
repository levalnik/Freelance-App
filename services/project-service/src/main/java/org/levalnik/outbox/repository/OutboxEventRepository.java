package org.levalnik.outbox.repository;

import org.levalnik.outbox.model.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, UUID> {
    List<OutboxEvent> findTop100ByProcessedFalseAndEventTypeOrderByCreatedAtAsc(String eventType);
}
