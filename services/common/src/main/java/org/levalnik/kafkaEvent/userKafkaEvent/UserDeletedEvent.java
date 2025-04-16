package org.levalnik.kafkaEvent.userKafkaEvent;

import org.levalnik.enums.userEnum.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDeletedEvent {
    private UUID userId;
    private LocalDateTime deletedAt;
    private UserRole userRole;
}