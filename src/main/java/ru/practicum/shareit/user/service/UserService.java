package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserPatchDto;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;

import java.util.List;

public interface UserService {
    List<UserResponseDto> getUsers();

    UserResponseDto getById(Long userId);

    UserResponseDto save(UserRequestDto userDto);

    UserResponseDto change(Long userId, UserPatchDto userDto);

    void delete(Long userId);
}
