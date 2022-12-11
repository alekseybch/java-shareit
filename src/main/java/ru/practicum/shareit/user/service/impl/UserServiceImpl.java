package ru.practicum.shareit.user.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.db.model.User;
import ru.practicum.shareit.user.db.repository.UserRepository;
import ru.practicum.shareit.user.dto.UserPatchDto;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public List<UserResponseDto> getUsers() {
        log.info("request to get all users.");
        return userRepository.readAll().stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }

    public UserResponseDto getById(Long userId) {
        log.info("request to get a user with id = {}.", userId);
        User user = userRepository.readById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("user with id = %d not found.", userId)));
        return userMapper.toUserDto(user);
    }

    public UserResponseDto save(UserRequestDto userDto) {
        log.info("request to save a user {}.", userDto);
        User user = userMapper.toUser(userDto);
        existsByEmail(user);
        User savedUser = userRepository.save(user);
        log.info("user with id = {} is saved {}.", savedUser.getId(), savedUser);
        return userMapper.toUserDto(savedUser);
    }

    public UserResponseDto change(Long userId, UserPatchDto userDto) {
        log.info("request to change a user with id = {} to {}.", userId, userDto);
        User dbUser = userRepository.readById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("user with id = %d not found.", userId)));
        User user = userMapper.toUser(userDto);
        if (user.getEmail() != null) {
            existsByEmail(user);
            dbUser.setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            dbUser.setName(user.getName());
        }
        User changedUser = userRepository.update(dbUser);
        log.info("user with id = {} is changed {}.", changedUser.getId(), changedUser);
        return userMapper.toUserDto(changedUser);
    }

    public void delete(Long userId) {
        log.info("request to delete a user with id = {}.", userId);
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("user with id = %d not found.", userId));
        }
        userRepository.delete(userId);
        log.info("user with id = {} is deleted.", userId);
    }

    private void existsByEmail(User user) {
        if (userRepository.existsByEmail(user)) {
            throw new DuplicateEmailException(String.format("user with email = %s already exist.", user.getEmail()));
        }
    }
}
