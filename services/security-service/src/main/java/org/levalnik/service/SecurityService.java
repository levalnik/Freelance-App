package org.levalnik.service;

import lombok.RequiredArgsConstructor;
import org.levalnik.client.UserClient;
import org.levalnik.dto.userDto.UserLoginRequest;
import org.levalnik.dto.userDto.UserRegisterRequest;
import org.levalnik.dto.userDto.UserResponse;
import org.levalnik.jwt.JwtCreator;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SecurityService {

    private final UserClient userClient;

    public UserResponse registerUser(UserRegisterRequest request) {
        UserResponse createdUser = userClient.registerUser(request);

        String token = JwtCreator.createToken(createdUser.getId(), createdUser.getRoles());

        return UserResponse.builder()
                .id(createdUser.getId())
                .username(createdUser.getUsername())
                .roles(createdUser.getRoles())
                .token(token)
                .build();
    }

    public UserResponse loginUser(UserLoginRequest request) {
        UserResponse user = userClient.getByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if(!user.getPassword().equals(request.getPassword())) {
            throw new RuntimeException("Wrong password");
        }

        String token = JwtCreator.createToken(user.getId(), user.getRoles());

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .roles(user.getRoles())
                .token(token)
                .build();
    }

    public String logout(String token) {
        return userClient.logout(token);
    }
}
