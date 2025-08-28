package org.example.shvidkiyhomework_int4.service;

import org.example.shvidkiyhomework_int4.dto.UserDto;
import org.example.shvidkiyhomework_int4.dto.UserRequestDto;
import org.example.shvidkiyhomework_int4.entity.UserEntity;

public interface UserMapper {
    UserDto entityToDto(UserEntity userEntity);

    UserEntity userRequestDtoToEntity(UserRequestDto userRequestDto);

    void updateEntity(UserEntity userEntity, UserRequestDto userRequestDto);
}
