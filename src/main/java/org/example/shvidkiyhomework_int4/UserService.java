package org.example.shvidkiyhomework_int4;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * @author Viktor Shvidkiy
 */
@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Transactional
    public UserDto createUser(UserRequestDto dto) {
        UserEntity user = userMapper.requestDtoToEntity(dto);
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
    public Optional<UserDto> getUserById(Integer id) {
        return userRepository.findById(id)
                .map(userMapper::entityToDto);
    }

    @Transactional
    public UserDto updateUser(Integer id, UserRequestDto dto) {
        return userRepository.findById(id)
                .map(user -> {
                    userMapper.updateEntity(user, dto);
                    return userMapper.entityToDto(userRepository.save(user));
                })
                .orElseThrow(() -> new RuntimeException("ID не найден."));
    }

    @Transactional
    public void deleteUser(Integer id){
        userRepository.deleteById(id);
    }
}
