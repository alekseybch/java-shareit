package ru.practicum.shareit.user.mapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.db.model.User;
import ru.practicum.shareit.user.dto.UserResponseDto;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserRequestDto dto);

    UserResponseDto toUserDto(User user);
}
