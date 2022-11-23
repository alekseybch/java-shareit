package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.db.model.Item;

import java.util.List;

public interface ItemService {
    List<Item> getItems(Long userId);

    Item getById(Long itemId);

    List<Item> getByText(String text);

    Item save(Long userId, Item item);

    Item change(Long userId, Long itemId, Item item);
}
