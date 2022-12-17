package ru.practicum.shareit.item.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.db.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findCommentsByItemId(Long itemId);

    @Query("select c from Comment c " +
            "where c.item.id in (:itemIds)")
    List<Comment> findCommentsByItemId(List<Long> itemIds);
}
