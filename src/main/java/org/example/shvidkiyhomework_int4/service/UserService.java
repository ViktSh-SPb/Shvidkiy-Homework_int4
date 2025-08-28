package org.example.shvidkiyhomework_int4.service;

import org.example.shvidkiyhomework_int4.dto.UserDto;
import org.example.shvidkiyhomework_int4.dto.UserRequestDto;

import java.util.List;
import java.util.Optional;

public interface UserService {
    UserDto createUser(UserRequestDto dto);
    List<UserDto> getAllUsers();
    UserDto getUserById(Integer id);
    UserDto updateUser(Integer id, UserRequestDto dto);
    void deleteUser(Integer id);
}
