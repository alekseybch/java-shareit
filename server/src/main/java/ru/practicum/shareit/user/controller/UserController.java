package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserPatchDto;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserResponseDto> getUsers() {
        return userService.getUsers();
    }

    @GetMapping(value = "/{userId}")
    public UserResponseDto getUser(@PathVariable Long userId) {
        return userService.getUser(userId);
    }

    @PostMapping
    public UserResponseDto createUser(@RequestBody UserRequestDto userDto) {
        return userService.createUser(userDto);
    }

    @PatchMapping(value = "/{id}")
    public UserResponseDto updateUser(@PathVariable("id") Long userId,
                                      @RequestBody UserPatchDto userDto) {
        return userService.updateUser(userId, userDto);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable("id") Long userId) {
        userService.deleteUser(userId);
    }
}
