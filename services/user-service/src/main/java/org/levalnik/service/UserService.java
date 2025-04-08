package org.levalnik.service;

import org.levalnik.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.levalnik.DTO.UserDTO;
import org.levalnik.exception.EntityNotFoundException;
import org.levalnik.model.User;
import org.levalnik.DTO.events.*;
import org.levalnik.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final KafkaProducerService kafkaProducerService;

    @Transactional(readOnly = true)
    public Optional<UserDTO> findByUsername(String username) {
        log.info("Searching for user with username: {}", username);
        return userRepository.findByUsername(username)
                .map(userMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public Optional<UserDTO> findByEmail(String email) {
        log.info("Searching for user with email: {}", email);
        return userRepository.findByEmail(email)
                .map(userMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Transactional(readOnly = true)
    public Page<UserDTO> findAll(Pageable pageable) {
        log.info("Fetching all users with pagination");
        return userRepository.findAll(pageable)
                .map(userMapper::toDTO);
    }

    @Transactional
    public UserDTO save(UserDTO userDTO) {
        if (existsByUsername(userDTO.getUsername()) || existsByEmail(userDTO.getEmail())) {
            throw new IllegalArgumentException("Username or email already exists");
        }
        User user = userMapper.toEntity(userDTO);
        User savedUser = userRepository.save(user);
        log.info("Saved new user with ID: {}", savedUser.getId());

        kafkaProducerService.sendUserCreatedEvent(
            UserCreatedEvent.builder()
                .userId(savedUser.getId())
                .username(savedUser.getUsername())
                .userRole(savedUser.getRole())
                .createdAt(LocalDateTime.now())
                .build()
        );

        return userMapper.toDTO(savedUser);
    }

    @Transactional
    public UserDTO updateUser(UUID id, UserDTO userDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + id));

        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setRole(userDTO.getRole());

        User updatedUser = userRepository.save(user);
        log.info("Updated user with ID: {}", updatedUser.getId());

        kafkaProducerService.sendUserUpdatedEvent(
            UserUpdatedEvent.builder()
                .userId(updatedUser.getId())
                .username(updatedUser.getUsername())
                .userRole(updatedUser.getRole())
                .updatedAt(LocalDateTime.now())
                .build()
        );

        return userMapper.toDTO(updatedUser);
    }

    @Transactional
    public void deleteById(UUID id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + id));

        kafkaProducerService.sendUserDeletedEvent(
            UserDeletedEvent.builder()
                .userId(id)
                .userRole(user.getRole())
                .deletedAt(LocalDateTime.now())
                .build()
        );

        userRepository.deleteById(id);
        log.info("Deleted user with ID: {}", id);
    }

    @Transactional
    public void updateProjectCount(UUID clientId) {
        log.info("Updating project count for client: {}", clientId);
        userRepository.findById(clientId)
            .ifPresent(user -> {
                user.setProjectCount(user.getProjectCount() + 1);
                userRepository.save(user);
                log.info("Updated project count for client: {}", clientId);
            });
    }

    @Transactional
    public void updateBidCount(UUID freelancerId) {
        log.info("Updating bid count for freelancer: {}", freelancerId);
        userRepository.findById(freelancerId)
            .ifPresent(user -> {
                user.setBidCount(user.getBidCount() + 1);
                userRepository.save(user);
                log.info("Updated bid count for freelancer: {}", freelancerId);
            });
    }
}