package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.db.model.User;

import java.util.List;

public interface UserService {
    List<User> getUsers();

    User getById(Long userId);

    User save(User user);

    User change(Long userId, User user);

    void delete(Long userId);
}
