package org.levalnik.dto.userDto;

import lombok.Data;

@Data
public class UserLoginRequest {
    private String username;
    private String password;
}
