package ru.practicum.shareit.user.db.repository.impl;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.db.model.User;
import ru.practicum.shareit.user.db.repository.UserRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Repository
public class InMemoryUserRepositoryImpl implements UserRepository {
    private final HashMap<Long, User> userStorage = new HashMap<>();
    private Long id = 0L;

    @Override
    public List<User> readAll() {
        return new ArrayList<>(userStorage.values());
    }

    @Override
    public Optional<User> readById(Long userId) {
        return Optional.ofNullable(userStorage.get(userId));
    }

    @Override
    public User save(User user) {
        generateId();
        user.setId(id);
        userStorage.put(user.getId(), user);
        return userStorage.get(user.getId());
    }

    @Override
    public User update(User user) {
        userStorage.put(user.getId(), user);
        return userStorage.get(user.getId());
    }

    @Override
    public void delete(Long userId) {
        userStorage.remove(userId);
    }

    @Override
    public boolean existsById(Long userId) {
        return userStorage.containsKey(userId);
    }

    @Override
    public boolean existsByEmail(User user) {
        return userStorage.values().stream()
                .filter(p -> !p.equals(user))
                .anyMatch(p -> p.getEmail().equals(user.getEmail()));
    }

    private void generateId() {
        id++;
    }
}
