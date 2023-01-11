package ru.practicum.shareit.item.db.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.db.model.Comment;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CommentRepositoryTest {
    @Autowired
    private CommentRepository commentRepository;

    private final Long itemId = 1L;
    private final Long itemNotFound = 4L;

    @Test
    void findCommentsByItemId_whenItemIdFound_thenReturnedItems() {
        List<Comment> comments = commentRepository.findCommentsByItemId(itemId);
        assertEquals(1, comments.size());
        assertEquals(itemId, comments.get(0).getItem().getId());

        comments = commentRepository.findCommentsByItemId(itemNotFound);
        assertEquals(0, comments.size());
    }

    @Test
    void findCommentsByItemIds__whenItemIdsFound_thenReturnedItems() {
        List<Long> itemIds = List.of(itemId, itemNotFound);

        List<Comment> comments = commentRepository.findCommentsByItemIds(itemIds);
        assertEquals(1, comments.size());
        assertEquals(itemId, comments.get(0).getItem().getId());
    }
}