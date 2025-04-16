package org.levalnik.kafkaEvent.userKafkaEvent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.levalnik.enums.userEnum.UserRole;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdatedEvent {
    private UUID userId;
    private String username;
    private String email;
    private UserRole userRole;
    private LocalDateTime updatedAt;
} 