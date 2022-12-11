package ru.practicum.shareit.user.mapper.impl;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.dto.UserPatchDto;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.db.model.User;
import ru.practicum.shareit.user.mapper.UserMapper;

@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public User toUser(UserRequestDto dto) {
        if (dto == null) {
            return null;
        }

        User user = new User();

        user.setName(dto.getName());
        user.setEmail(dto.getEmail());

        return user;
    }

    @Override
    public User toUser(UserPatchDto dto) {
        if (dto == null) {
            return null;
        }

        User user = new User();

        user.setName(dto.getName());
        user.setEmail(dto.getEmail());

        return user;
    }

    @Override
    public UserResponseDto toUserDto(User user) {
        if (user == null) {
            return null;
        }

        UserResponseDto userDto = new UserResponseDto();

        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());

        return userDto;
    }
}
