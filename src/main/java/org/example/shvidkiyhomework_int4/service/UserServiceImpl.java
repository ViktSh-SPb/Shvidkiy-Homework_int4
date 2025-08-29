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
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Transactional
    public UserDto createUser(UserRequestDto dto) {
        UserEntity user = userMapper.userRequestDtoToEntity(dto);
        user.setCreatedAt(LocalDateTime.now());
        return userMapper.entityToDto(userRepository.save(user));
    }

    @Transactional
    public List<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::entityToDto)
                .toList();
    }

    @Transactional
    public UserDto getUserById(Integer id) {
        return userRepository.findById(id)
                .map(userMapper::entityToDto)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с ID: " + id + " не найден."));
    }

    @Transactional
    public UserDto updateUser(Integer id, UserRequestDto dto) {
        return userRepository.findById(id)
                .map(user -> {
                    userMapper.updateEntity(user, dto);
                    return userMapper.entityToDto(userRepository.save(user));
                })
                .orElseThrow(() -> new UserNotFoundException("Пользователь с ID: " + id + " не найден."));
    }

    @Transactional
    public void deleteUser(Integer id) {
        userRepository.deleteById(id);
    }
}
