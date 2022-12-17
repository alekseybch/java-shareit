package ru.practicum.shareit.item.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.global.mapper.EntityMapper;
import ru.practicum.shareit.item.db.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> getItemsByOwnerId(Long userId);

    @Query("select i " +
            "from Item i " +
            "where i.available = true " +
            "and (lower(i.name) like %:text% " +
            "or lower(i.description) like %:text%)")
    List<Item> getByText(String text);

    @EntityMapper
    Item getItemById(Long id);
}