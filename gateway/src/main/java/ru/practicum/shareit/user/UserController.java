package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserPatchDto;
import ru.practicum.shareit.user.dto.UserRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated
@Slf4j
public class UserController {
    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getUsers() {
        log.info("request to get users");
        return userClient.getUsers();
    }

    @GetMapping(value = "/{userId}")
    public ResponseEntity<Object> getUser(@PathVariable Long userId) {
        log.info("request to get user userId = {}", userId);
        return userClient.getUser(userId);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> createUser(@RequestBody @Valid @NotNull UserRequestDto userDto) {
        log.info("request to creating user {}", userDto);
        return userClient.createUser(userDto);
    }

    @PatchMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> updateUser(@PathVariable("id") Long userId,
                                             @RequestBody @Valid @NotNull UserPatchDto userDto) {
        log.info("request to update user userId = {}", userId);
        return userClient.updateUser(userId, userDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable("id") Long userId) {
        log.info("request to delete user userId = {}", userId);
        return userClient.deleteUser(userId);
    }
}
