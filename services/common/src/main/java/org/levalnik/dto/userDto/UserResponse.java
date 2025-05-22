package org.levalnik.dto.userDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.levalnik.enums.userEnum.UserRole;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    private UUID id;
    private String username;
    private String password;
    private String token;
    private List<UserRole> roles;
}
