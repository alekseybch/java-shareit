package ru.practicum.shareit.item.db.repository.impl;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.db.model.Item;
import ru.practicum.shareit.item.db.repository.ItemRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class InMemoryItemRepositoryImpl implements ItemRepository {
    private final HashMap<Long, Item> itemStorage = new HashMap<>();
    private Long id = 0L;

    @Override
    public List<Item> readAll(Long userId) {
        return itemStorage.values().stream()
                .filter(p -> p.getOwner().getId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Item> readById(Long itemId) {
        return Optional.ofNullable(itemStorage.get(itemId));
    }

    @Override
    public List<Item> readByText(String text) {
        return itemStorage.values().stream()
                .filter(p -> p.getName().toLowerCase().contains(text) || p.getDescription().toLowerCase().contains(text))
                .filter(Item::getAvailable)
                .collect(Collectors.toList());
    }

    @Override
    public Item save(Item item) {
        generateId();
        item.setId(id);
        itemStorage.put(item.getId(), item);
        return itemStorage.get(item.getId());
    }

    @Override
    public Item update(Item item) {
        itemStorage.put(item.getId(), item);
        return itemStorage.get(item.getId());
    }

    private void generateId() {
        id++;
    }
}
