//package org.levalnik.mapper;
//
//import org.levalnik.enums.userEnum.UserStatus;
//import org.levalnik.dto.userDto.UserDTO;
//import org.levalnik.user.mapper.UserMapper;
//import org.levalnik.user.model.User;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.util.UUID;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class UserMapperTest {
//
//    private User user;
//    private UserDTO userDTO;
//
//    private final UserMapper mapper = UserMapper.INSTANCE;
//
//    @BeforeEach
//    void setUp() {
//        user = new User();
//        user.setId(UUID.randomUUID());
//        user.setUsername("leva_kuimov");
//        user.setEmail("leva_kuimov@example.com");
//        user.setFirstName("leva");
//        user.setLastName("kuimov");
//        user.setStatus(UserStatus.ACTIVE);
//
//        userDTO = new UserDTO();
//        userDTO.setId(UUID.randomUUID());
//        userDTO.setUsername("leva_kuimov");
//        userDTO.setEmail("leva_kuimov@example.com");
//        userDTO.setFirstName("leva");
//        userDTO.setLastName("kuimov");
//        userDTO.setStatus(UserStatus.ACTIVE);
//    }
//
//    @Test
//    void testToDTO() {
//        UserDTO dto = UserMapper.INSTANCE.toDTO(user);
//
//        assertNotNull(dto);
//        assertEquals(user.getId(), dto.getId());
//        assertEquals(user.getUsername(), dto.getUsername());
//        assertEquals(user.getEmail(), dto.getEmail());
//        assertEquals(user.getFirstName(), dto.getFirstName());
//        assertEquals(user.getLastName(), dto.getLastName());
//        assertEquals(user.getStatus(), dto.getStatus());
//    }
//
//    @Test
//    void testToEntity() {
//        User entity = mapper.toEntity(userDTO);
//
//        assertNotNull(entity);
//        assertEquals(userDTO.getId(), entity.getId());
//        assertEquals(userDTO.getUsername(), entity.getUsername());
//        assertEquals(userDTO.getEmail(), entity.getEmail());
//        assertEquals(userDTO.getFirstName(), entity.getFirstName());
//        assertEquals(userDTO.getLastName(), entity.getLastName());
//        assertEquals(userDTO.getStatus(), entity.getStatus());
//    }
//}