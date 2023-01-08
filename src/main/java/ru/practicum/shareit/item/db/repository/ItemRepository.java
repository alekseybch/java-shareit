package ru.practicum.shareit.item.db.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.global.mapper.EntityMapper;
import ru.practicum.shareit.item.db.model.Item;

import java.util.List;
import java.util.Set;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> getItemsByOwnerId(Long userId, Pageable pageable);

    @Query("select i " +
            "from Item i " +
            "where i.available = true " +
            "and (lower(i.name) like concat('%', :text, '%') " +
            "or lower(i.description) like concat('%', :text, '%'))")
    Page<Item> getByText(String text, Pageable pageable);

    @Query("select i from Item i " +
            "where i.request.id in (:itemReqIds)")
    List<Item> findItemsByRequestId(Set<Long> itemReqIds);

    List<Item> getItemsByRequestId(Long requestId);

    @EntityMapper
    Item getItemById(Long id);
}