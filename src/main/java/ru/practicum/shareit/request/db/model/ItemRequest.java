package ru.practicum.shareit.request.db.model;

import lombok.Data;
import ru.practicum.shareit.user.db.model.User;

import java.time.LocalDateTime;

@Data
public class ItemRequest {
    private Long id;
    private String description;
    private User requestor;
    private LocalDateTime created;
}
