package org.example.shvidkiyhomework_int4;

import org.example.shvidkiyhomework_int4.dto.UserDto;
import org.example.shvidkiyhomework_int4.dto.UserRequestDto;
import org.example.shvidkiyhomework_int4.entity.UserEntity;
import org.example.shvidkiyhomework_int4.service.UserMapperImpl;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Viktor Shvidkiy
 */
public class UserMapperImplTest {
    private final UserMapperImpl userMapperImpl = new UserMapperImpl();

    @Test
    void testEntityToDto(){
        LocalDateTime time = LocalDateTime.now();

        UserEntity entity = UserEntity.builder()
                .id(1)
                .name("Jack")
                .email("jack@gmail.com")
                .age(20)
                .createdAt(time)
                .build();

        UserDto dto = userMapperImpl.entityToDto(entity);

        assertAll(
                ()->assertEquals(entity.getId(), dto.getId()),
                ()->assertEquals(entity.getName(), dto.getName()),
                ()->assertEquals(entity.getEmail(), dto.getEmail()),
                ()->assertEquals(entity.getAge(), dto.getAge()),
                ()->assertEquals(entity.getCreatedAt().toString(), dto.getCreatedAt())

        );
    }

    @Test
    void testRequestDtoToEntity(){
        UserRequestDto requestDto = UserRequestDto.builder()
                .name("Jack")
                .email("jack@gmail.com")
                .age(20)
                .build();

        UserEntity entity = userMapperImpl.userRequestDtoToEntity(requestDto);

        assertAll(
                ()->assertNull(entity.getId()),
                ()->assertEquals(entity.getName(), requestDto.getName()),
                ()->assertEquals(entity.getEmail(), requestDto.getEmail()),
                ()->assertEquals(entity.getAge(), requestDto.getAge()),
                ()->assertNull(entity.getCreatedAt())
        );
    }

    @Test
    void testUpdateEntity(){
        UserEntity entity = UserEntity.builder()
                .id(1)
                .name("TomBefore")
                .email("before@gmail.com")
                .age(19)
                .build();

        UserRequestDto updateDto = UserRequestDto.builder()
                .name("TomAfter")
                .email("after@gmail.com")
                .age(20)
                .build();

        userMapperImpl.updateEntity(entity, updateDto);

        assertAll(
                ()->assertEquals("TomAfter", entity.getName()),
                ()->assertEquals("after@gmail.com", entity.getEmail()),
                ()->assertEquals(20, entity.getAge())
        );
    }
}
