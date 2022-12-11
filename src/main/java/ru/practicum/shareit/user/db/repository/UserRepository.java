package ru.practicum.shareit.user.db.repository;

import ru.practicum.shareit.user.db.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    List<User> readAll();

    Optional<User> readById(Long userId);

    User save(User user);

    User update(User user);

    void delete(Long userId);

    boolean existsById(Long userId);

    boolean existsByEmail(User user);
}
