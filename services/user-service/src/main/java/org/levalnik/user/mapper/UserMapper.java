package org.levalnik.user.mapper;

import org.levalnik.dto.userDto.UserLoginRequest;
import org.levalnik.dto.userDto.UserRegisterRequest;
import org.levalnik.dto.userDto.UserResponse;
import org.levalnik.user.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserResponse toDTO(User user);

    User toEntity(UserRegisterRequest userDTO);

//    User toEntity(UserLoginRequest userDTO);
}