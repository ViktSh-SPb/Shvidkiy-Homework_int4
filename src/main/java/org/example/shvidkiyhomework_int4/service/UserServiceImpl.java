package org.example.shvidkiyhomework_int4.service;

import org.example.shvidkiyhomework_int4.entity.UserEntity;
import org.example.shvidkiyhomework_int4.exception.UserNotFoundException;
import org.example.shvidkiyhomework_int4.repository.UserRepository;
import org.example.shvidkiyhomework_int4.dto.UserDto;
import org.example.shvidkiyhomework_int4.dto.UserRequestDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * @author Viktor Shvidkiy
 */
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapperImpl userMapperImpl;

    public UserServiceImpl(UserRepository userRepository, UserMapperImpl userMapperImpl) {
        this.userRepository = userRepository;
        this.userMapperImpl = userMapperImpl;
    }

    @Transactional
    public UserDto createUser(UserRequestDto dto) {
        UserEntity user = userMapperImpl.userRequestDtoToEntity(dto);
        user.setCreatedAt(LocalDateTime.now());
        return userMapperImpl.entityToDto(userRepository.save(user));
    }

    @Transactional
    public List<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapperImpl::entityToDto)
                .toList();
    }

    @Transactional
    public UserDto getUserById(Integer id) {
        return userRepository.findById(id)
                .map(userMapperImpl::entityToDto)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с ID: " + id + " не найден."));
    }

    @Transactional
    public UserDto updateUser(Integer id, UserRequestDto dto) {
        return userRepository.findById(id)
                .map(user -> {
                    userMapperImpl.updateEntity(user, dto);
                    return userMapperImpl.entityToDto(userRepository.save(user));
                })
                .orElseThrow(() -> new RuntimeException("ID не найден."));
    }

    @Transactional
    public void deleteUser(Integer id) {
        userRepository.deleteById(id);
    }
}
