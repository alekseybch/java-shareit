package ru.practicum.shareit.user.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
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

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDto> getUsers() {
        log.info("request to get all users.");
        return userRepository.findAll().stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getById(Long userId) {
        log.info("request to get a user with id = {}.", userId);
        return userMapper.toUserDto(userRepository.getReferenceById(userId));
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public UserResponseDto save(UserRequestDto userDto) {
        log.info("request to save a user {}.", userDto);
        User user = userMapper.toUser(userDto);
        User savedUser = userRepository.save(user);
        log.info("user with id = {} is saved {}.", savedUser.getId(), savedUser);
        return userMapper.toUserDto(savedUser);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public UserResponseDto change(Long userId, UserPatchDto userDto) {
        log.info("request to change a user with id = {} to {}.", userId, userDto);
        User dbUser = userRepository.getReferenceById(userId);
        if (userDto.getEmail() != null) {
            dbUser.setEmail(userDto.getEmail());
        }
        if (userDto.getName() != null) {
            dbUser.setName(userDto.getName());
        }
        User changedUser = userRepository.save(dbUser);
        log.info("user with id = {} is changed {}.", changedUser.getId(), changedUser);
        return userMapper.toUserDto(changedUser);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void delete(Long userId) {
        log.info("request to delete a user with id = {}.", userId);
        userRepository.deleteById(userId);
        log.info("user with id = {} is deleted.", userId);
    }
}
