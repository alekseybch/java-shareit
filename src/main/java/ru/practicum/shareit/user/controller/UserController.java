package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.db.model.User;
import ru.practicum.shareit.user.dto.UserPatchDto;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping
    public List<UserResponseDto> getUsers() {
        List<User> users = userService.getUsers();
        return users.stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @GetMapping(value = "/{userId}")
    public UserResponseDto findUserById(@PathVariable Long userId) {
        return userMapper.toUserDto(userService.getById(userId));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDto saveUser(@RequestBody @Valid @NotNull UserRequestDto userDto) {
        User user = userMapper.toUser(userDto);
        return userMapper.toUserDto(userService.save(user));
    }

    @PatchMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public UserResponseDto changeUser(@PathVariable("id") Long userId,
                                      @RequestBody @Valid @NotNull UserPatchDto userDto) {
        User user = userMapper.toUser(userDto);
        return userMapper.toUserDto(userService.change(userId, user));
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable("id") @NotNull Long userId) {
        userService.delete(userId);
    }
}
