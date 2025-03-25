package org.levalnik.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.levalnik.DTO.UserDTO;
import org.levalnik.exception.EntityNotFoundException;
import org.levalnik.mapper.UserMapper;
import org.levalnik.model.User;
import org.levalnik.repository.UserRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");

        userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setUsername("testuser");
        userDTO.setEmail("test@example.com");
    }

    @Test
    void testFindByUsername_UserExists() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(userMapper.toDTO(user)).thenReturn(userDTO);

        Optional<UserDTO> result = userService.findByUsername("testuser");

        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
    }

    @Test
    void testFindByUsername_UserNotFound() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        Optional<UserDTO> result = userService.findByUsername("testuser");

        assertFalse(result.isPresent());
    }

    @Test
    void testFindAll_ReturnsUsers() {
        Page<User> userPage = new PageImpl<>(List.of(user));
        when(userRepository.findAll(PageRequest.of(0, 10))).thenReturn(userPage);
        when(userMapper.toDTO(user)).thenReturn(userDTO);

        Page<UserDTO> result = userService.findAll(PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
        assertEquals("testuser", result.getContent().get(0).getUsername());
    }

    @Test
    void testSave_NewUser_Success() {
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(userMapper.toEntity(userDTO)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDTO(user)).thenReturn(userDTO);

        UserDTO savedUser = userService.save(userDTO);

        assertEquals("testuser", savedUser.getUsername());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testSave_UserAlreadyExists_ThrowsException() {
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> userService.save(userDTO));
    }

    @Test
    void testUpdateUser_UserExists_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDTO(user)).thenReturn(userDTO);

        UserDTO updatedUser = userService.updateUser(1L, userDTO);

        assertEquals("testuser", updatedUser.getUsername());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testUpdateUser_UserNotFound_ThrowsException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.updateUser(1L, userDTO));
    }

    @Test
    void testDeleteById_UserExists_Success() {
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);

        assertDoesNotThrow(() -> userService.deleteById(1L));
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteById_UserNotFound_ThrowsException() {
        when(userRepository.existsById(1L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> userService.deleteById(1L));
    }
}