package org.levalnik.dto.user;

import lombok.Builder;
import lombok.Data;
import org.levalnik.enums.userEnum.UserRole;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class UserResponse {
    private UUID id;
    private String username;
    private String password;
    private String token;
    private List<UserRole> roles;
}
