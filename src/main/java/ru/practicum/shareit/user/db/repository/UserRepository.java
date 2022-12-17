package ru.practicum.shareit.user.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.global.mapper.EntityMapper;
import ru.practicum.shareit.user.db.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @EntityMapper
    User getUserById(Long id);
}