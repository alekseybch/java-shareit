package ru.practicum.shareit.user.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.db.model.User;
import ru.practicum.shareit.user.db.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public List<User> getUsers() {
        log.info("request to get all users.");
        return userRepository.readAll();
    }

    public User getById(Long userId) {
        log.info("request to get a user with id = {}.", userId);
        return userRepository.readById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("user with id = %d not found.", userId)));
    }

    public User save(User user) {
        log.info("request to save a user {}.", user);
        existsByEmail(user);
        User savedUser = userRepository.save(user);
        log.info("user with id = {} is saved {}.", savedUser.getId(), savedUser);
        return savedUser;
    }

    public User change(Long userId, User user) {
        User dbUser = getById(userId);
        log.info("request to change a user with id = {} to {}.", userId, user);
        if (user.getEmail() != null) {
            existsByEmail(user);
            dbUser.setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            dbUser.setName(user.getName());
        }
        User changedUser = userRepository.update(dbUser);
        log.info("user with id = {} is changed {}.", changedUser.getId(), changedUser);
        return changedUser;
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
