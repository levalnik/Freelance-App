package org.levalnik.client;

import lombok.RequiredArgsConstructor;
import org.levalnik.dto.userDto.UserRegisterRequest;
import org.levalnik.dto.userDto.UserResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserClient {
    private final WebClient webClient;
    public Optional<UserResponse> getByUsername(String username) {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/users/username/{username}")
                        .build(username))
                .retrieve()
                .bodyToMono(UserResponse.class)
                .onErrorResume(e -> Mono.empty())
                .blockOptional();
    }

    public UserResponse registerUser(UserRegisterRequest request) {
        return webClient.post()
                .uri("/users")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(UserResponse.class)
                .block();
    }

    public String logout(String token) {
        return webClient.post()
                .uri("/users/logout")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
