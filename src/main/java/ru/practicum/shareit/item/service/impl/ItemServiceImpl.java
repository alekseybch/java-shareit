package ru.practicum.shareit.item.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotUserInstanceException;
import ru.practicum.shareit.item.db.model.Item;
import ru.practicum.shareit.item.db.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.db.model.User;
import ru.practicum.shareit.user.db.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public List<Item> getItems(Long userId) {
        log.info("request to get all user items with id = {}.", userId);
        existUserById(userId);
        return itemRepository.readAll(userId);
    }

    public Item getById(Long itemId) {
        log.info("request to get a item with id = {}.", itemId);
        return itemRepository.readById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("item with id = %d not found.", itemId)));
    }

    public List<Item> getByText(String text) {
        log.info("items search request by text = {}.", text);
        if ( text.isEmpty() ) {
            return new ArrayList<>();
        }
        return itemRepository.readByText(text.toLowerCase());
    }

    public Item save(Long userId, Item item) {
        log.info("request to save a item {}.", item);
        User user = userRepository.readById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("user with id = %d not found.", userId)));
        item.setOwner(user);
        Item savedItem = itemRepository.save(item);
        log.info("item with id = {} is saved {}.", savedItem.getId(), savedItem);
        return savedItem;
    }

    public Item change(Long userId, Long itemId, Item item) {
        log.info("request to change a item with id = {} to {}.", itemId, item);
        existUserById(userId);
        Item dbItem = getById(itemId);
        if ( !dbItem.getOwner().getId().equals(userId) ) {
            throw new NotUserInstanceException(String.format("user with id = %d does not own item with id = %d.", userId, itemId));
        }
        if ( item.getName() != null ) {
            dbItem.setName(item.getName());
        }
        if ( item.getDescription() != null ) {
            dbItem.setDescription(item.getDescription());
        }
        if ( item.getAvailable() != null ) {
            dbItem.setAvailable(item.getAvailable());
        }
        Item changedItem = itemRepository.update(dbItem);
        log.info("item with id = {} is changed {}.", changedItem.getId(), changedItem);
        return changedItem;
    }

    private void existUserById(Long userId) {
        if ( !userRepository.existsById(userId) ) {
            throw new NotFoundException(String.format("user with id = %d not found.", userId));
        }
    }
}
