package ru.practicum.shareit.user.mapper;

import ru.practicum.shareit.user.dto.UserPatchDto;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.db.model.User;
import ru.practicum.shareit.user.dto.UserResponseDto;

public interface UserMapper {
    User toUser(UserRequestDto dto);

    User toUser(UserPatchDto dto);

    UserResponseDto toUserDto(User user);
}
