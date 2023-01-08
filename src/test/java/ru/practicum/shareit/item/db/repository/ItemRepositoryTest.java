package ru.practicum.shareit.item.db.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.db.model.Item;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;

    private final Long requestId = 1L;
    private final Pageable pageable = PageRequest.of(0, 10, Sort.Direction.ASC, "id");

    @Test
    void getItemsByOwnerId_whenOwnerIdFound_thenReturnedItems() {
        Long user1 = 1L;

        List<Item> items = itemRepository.getItemsByOwnerId(user1, pageable);
        assertEquals(1, items.size());
        assertEquals(user1, items.get(0).getOwner().getId());

        Long userNotFound = 4L;
        items = itemRepository.getItemsByOwnerId(userNotFound, pageable);
        assertEquals(0, items.size());
    }

    @Test
    void getByText_whenTextFound_thenReturnedItems() {
        String text = "drill";

        Page<Item> items = itemRepository.getByText(text, pageable);
        assertEquals(1, items.getContent().size());
        assertEquals(text, items.getContent().get(0).getName());

        items = itemRepository.getByText("not found", pageable);
        assertEquals(0, items.getContent().size());
    }

    @Test
    void findItemsByRequestId_whenRequestFound_thenReturnedItems() {
        Set<Long> requestIds = Set.of(requestId, 2L);

        List<Item> items = itemRepository.findItemsByRequestId(requestIds);
        assertEquals(1, items.size());
        assertEquals(requestId, items.get(0).getRequest().getId());
    }

    @Test
    void getItemsByRequestId_whenRequestFound_thenReturnedItems() {
        List<Item> items = itemRepository.getItemsByRequestId(requestId);
        assertEquals(1, items.size());
        assertEquals(requestId, items.get(0).getRequest().getId());
    }
}