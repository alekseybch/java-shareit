package ru.practicum.shareit.item.db.model;

import lombok.Data;
import ru.practicum.shareit.request.db.model.ItemRequest;
import ru.practicum.shareit.user.db.model.User;

@Data
public class Item {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private User owner;
    private ItemRequest request;
}
