package ru.practicum.shareit.request.db.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.global.mapper.EntityMapper;
import ru.practicum.shareit.request.db.model.ItemRequest;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    Page<ItemRequest> getItemRequestsByRequestorId(Long userId, Pageable pageable);

    @Query("select ir from ItemRequest ir " +
            "where ir.requestor.id <> :userId")
    Page<ItemRequest> findAllByOtherUsers(Long userId, Pageable pageable);

    @EntityMapper
    ItemRequest getItemRequestById(Long id);
}
