package org.levalnik.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.levalnik.dto.userDto.UserLoginRequest;
import org.levalnik.dto.userDto.UserRegisterRequest;
import org.levalnik.dto.userDto.UserResponse;
import org.levalnik.service.SecurityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class SecurityController {
    private final SecurityService securityService;

    @GetMapping("/login")
    public ResponseEntity<UserResponse> login(@Valid @RequestBody UserLoginRequest userLoginRequest) {
        UserResponse response = securityService.loginUser(userLoginRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody UserRegisterRequest userRegisterRequest) {
        UserResponse response = securityService.registerUser(userRegisterRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token) {
        String message = securityService.logout(token);
        return ResponseEntity.ok(message);
    }
}
