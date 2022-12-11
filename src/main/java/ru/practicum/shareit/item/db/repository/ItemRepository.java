package ru.practicum.shareit.item.db.repository;

import ru.practicum.shareit.item.db.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    List<Item> readAll(Long userId);

    Optional<Item> readById(Long itemId);

    List<Item> readByText(String text);

    Item save(Item item);

    Item update(Item item);
}
