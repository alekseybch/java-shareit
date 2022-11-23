package ru.practicum.shareit.user.db.model;

import lombok.Data;

@Data
public class User {
    private Long id;
    private String name;
    private String email;
}
