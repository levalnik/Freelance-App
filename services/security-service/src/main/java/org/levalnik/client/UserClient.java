package org.levalnik.client;

import lombok.RequiredArgsConstructor;
import org.levalnik.dto.user.UserRegisterRequest;
import org.levalnik.dto.user.UserResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserClient {
    private final WebClient webClient;

    @Value("${service.user-service.url}")
    private String userServiceUrl;

    public Optional<UserResponse> getByUsername(String username) {
        return webClient.get()
                .uri(userServiceUrl + "/username/{username}")
                .retrieve()
                .bodyToMono(UserResponse.class)
                .onErrorResume(e -> Mono.empty())
                .blockOptional();
    }

    public UserResponse registerUser(UserRegisterRequest request) {
        return webClient.post()
                .uri(userServiceUrl + "/internal/users/register")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(UserResponse.class)
                .block();
    }

    public String logout(String token) {
        return webClient.post()
                .uri(userServiceUrl + "/internal/users/logout")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
