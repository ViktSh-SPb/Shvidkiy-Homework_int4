package org.example.shvidkiyhomework_int4;

import org.example.shvidkiyhomework_int4.dto.UserDto;
import org.example.shvidkiyhomework_int4.dto.UserRequestDto;
import org.example.shvidkiyhomework_int4.entity.UserEntity;
import org.example.shvidkiyhomework_int4.repository.UserRepository;
import org.example.shvidkiyhomework_int4.service.UserMapperImpl;
import org.example.shvidkiyhomework_int4.service.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * @author Viktor Shvidkiy
 */

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapperImpl userMapperImpl;

    @InjectMocks
    private UserServiceImpl userServiceImpl;

    private UserEntity userEntity;
    private UserDto userDto;
    private UserRequestDto userRequestDto;

    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);

        userEntity = UserEntity.builder()
                .id(1)
                .name("Jack")
                .email("jack@gmail.com")
                .age(20)
                .createdAt(LocalDateTime.now())
                .build();

        userDto = UserDto.builder()
                .id(1)
                .name("Jack")
                .email("jack@gmail.com")
                .age(20)
                .createdAt(userEntity.getCreatedAt().toString())
                .build();

        userRequestDto = UserRequestDto.builder()
                .name("Jack")
                .email("jack@gmail.com")
                .age(20)
                .build();
    }

    @Test
    void testCreateuser(){
        when(userMapperImpl.userRequestDtoToEntity(userRequestDto)).thenReturn(userEntity);
        when(userRepository.save(userEntity)).thenReturn(userEntity);
        when(userMapperImpl.entityToDto(userEntity)).thenReturn(userDto);

        UserDto result = userServiceImpl.createUser(userRequestDto);

        assertAll(
                ()->assertNotNull(result),
                ()->assertEquals(userDto.getId(), result.getId())
        );
        verify(userRepository).save(userEntity);
    }

    @Test
    void testGetAllUsers(){
        List<UserEntity> users = Arrays.asList(userEntity);

        when(userRepository.findAll()).thenReturn(users);
        when(userMapperImpl.entityToDto(userEntity)).thenReturn(userDto);

        List<UserDto> result = userServiceImpl.getAllUsers();

        assertAll(
                ()->assertEquals(1, result.size()),
                ()->assertEquals(userDto.getId(), result.get(0).getId())
        );
        verify(userRepository).findAll();
    }

    @Test
    void testGetAllUsersEmpty(){
        when(userRepository.findAll()).thenReturn(List.of());

        List<UserDto> result = userServiceImpl.getAllUsers();

        assertAll(
                ()->assertNotNull(result),
                ()->assertTrue(result.isEmpty())
        );
        verify(userRepository).findAll();
    }

    @Test
    void testGetUserByIdFound(){
        when(userRepository.findById(1)).thenReturn(Optional.of(userEntity));
        when(userMapperImpl.entityToDto(userEntity)).thenReturn(userDto);

        Optional<UserDto> result = userServiceImpl.getUserById(1);

        assertAll(
                ()->assertTrue(result.isPresent()),
                ()->assertEquals(userDto.getId(), result.get().getId())
        );
        verify(userRepository).findById(1);
    }

    @Test
    void testGetUserByIdNotFound(){
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        Optional<UserDto> result = userServiceImpl.getUserById(1);

        assertTrue(result.isEmpty());
        verify(userRepository).findById(1);
    }

    @Test
    void testUpdateuserFound(){
        UserRequestDto updateDto = UserRequestDto.builder()
                .name("MrJack")
                .email("ceo_jack@gmail.com")
                .age(21)
                .build();

        UserEntity updatedEntity = UserEntity.builder()
                .id(1)
                .name("MrJack")
                .email("ceo_jack@gmail.com")
                .age(21)
                .createdAt(userEntity.getCreatedAt())
                .build();

        UserDto updatedDto = UserDto.builder()
                .id(1)
                .name("MrJack")
                .email("ceo_jack@gmail.com")
                .age(21)
                .createdAt(updatedEntity.getCreatedAt().toString())
                .build();

        when(userRepository.findById(1)).thenReturn(Optional.of(userEntity));
        doAnswer(invocation -> {
            UserEntity entity = invocation.getArgument(0);
            entity.setName(updateDto.getName());
            entity.setEmail(updateDto.getEmail());
            entity.setAge(updateDto.getAge());
            return null;
        }).when(userMapperImpl).updateEntity(userEntity, updateDto);
        when(userRepository.save(userEntity)).thenReturn(updatedEntity);
        when(userMapperImpl.entityToDto(updatedEntity)).thenReturn(updatedDto);

        UserDto result = userServiceImpl.updateUser(1,updateDto);

        assertAll(
                ()->assertEquals("MrJack", result.getName()),
                ()->assertEquals("ceo_jack@gmail.com", result.getEmail()),
                ()->assertEquals(21,result.getAge())
        );
        verify(userRepository).findById(1);
        verify(userRepository).save(userEntity);
    }

    @Test
    void tesstUpdateUserNotFound(){
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, ()->{
            userServiceImpl.updateUser(1,userRequestDto);
        });
        assertEquals("ID не найден.", exception.getMessage());
        verify(userRepository).findById(1);
    }

    @Test
    void testDeleteUser(){
        doNothing().when(userRepository).deleteById(1);
        userServiceImpl.deleteUser(1);
        verify(userRepository).deleteById(1);
    }
}
