package ru.practicum.shareit.user.service.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserPatchDto;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserServiceImplIntTest {
    @Autowired
    UserService userService;

    @Test
    void getUsers() {
        List<UserResponseDto> users = userService.getUsers();

        assertEquals(users.size(), 4);
        assertEquals(users.get(0).getId(), 1L);
    }

    @Test
    void getById() {
        UserResponseDto userResponseDto = userService.getUser(1L);

        assertNotNull(userResponseDto);
        assertEquals(userResponseDto.getId(), 1L);
    }

    @Test
    void save() {
        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setName("new");
        userRequestDto.setEmail("new@new.com");

        UserResponseDto userResponseDto = userService.createUser(userRequestDto);

        assertNotNull(userResponseDto);
        assertEquals(userResponseDto.getId(), 5L);
        assertEquals(userResponseDto.getName(), "new");
        assertEquals(userResponseDto.getEmail(), "new@new.com");
    }

    @Test
    void change() {
        UserPatchDto userPatchDto = new UserPatchDto();
        userPatchDto.setName("changed");

        UserResponseDto userResponseDto = userService.updateUser(1L, userPatchDto);

        assertNotNull(userResponseDto);
        assertEquals(userResponseDto.getId(), 1L);
        assertEquals(userResponseDto.getName(), "changed");
    }
}