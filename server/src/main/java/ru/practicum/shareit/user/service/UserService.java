package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserPatchDto;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;

import java.util.List;

public interface UserService {
    List<UserResponseDto> getUsers();

    UserResponseDto getUser(Long userId);

    UserResponseDto createUser(UserRequestDto userDto);

    UserResponseDto updateUser(Long userId, UserPatchDto userDto);

    void deleteUser(Long userId);
}
