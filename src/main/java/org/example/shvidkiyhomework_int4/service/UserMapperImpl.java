package org.example.shvidkiyhomework_int4.service;

import org.example.shvidkiyhomework_int4.entity.UserEntity;
import org.example.shvidkiyhomework_int4.dto.UserDto;
import org.example.shvidkiyhomework_int4.dto.UserRequestDto;
import org.springframework.stereotype.Component;

/**
 * @author Viktor Shvidkiy
 */
@Component
public class UserMapperImpl implements UserMapper{
    public UserDto entityToDto(UserEntity entity){
        return UserDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .email(entity.getEmail())
                .age(entity.getAge())
                .createdAt(entity.getCreatedAt()!=null? entity.getCreatedAt().toString():null)
                .build();
    }

    public UserEntity userRequestDtoToEntity(UserRequestDto dto){
        return UserEntity.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .age(dto.getAge())
                .build();
    }

    public void updateEntity(UserEntity entity, UserRequestDto dto){
        entity.setName(dto.getName());
        entity.setEmail(dto.getEmail());
        entity.setAge(dto.getAge());
    }
}
