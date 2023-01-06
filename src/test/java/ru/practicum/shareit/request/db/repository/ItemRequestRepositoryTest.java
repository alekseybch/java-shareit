package ru.practicum.shareit.request.db.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.request.db.model.ItemRequest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemRequestRepositoryTest {
    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private final Long requestorId = 1L;
    private final Long requestorNotFound = 4L;
    private final Pageable pageable = PageRequest.of(0, 10, Sort.Direction.DESC, "created");

    @Test
    void getItemRequestsByRequestorId_whenRequestorIdFound_thenReturnedItemRequests() {
        Page<ItemRequest> requests = itemRequestRepository.getItemRequestsByRequestorId(requestorId, pageable);
        assertEquals(1, requests.getContent().size());
        assertEquals(requestorId, requests.getContent().get(0).getRequestor().getId());

        requests = itemRequestRepository.getItemRequestsByRequestorId(requestorNotFound, pageable);
        assertEquals(0, requests.getContent().size());
    }

    @Test
    void findAllByOtherUsers_whenBookerIdFound_thenReturnedBookings() {
        Long otherRequestor = 2L;

        Page<ItemRequest> requests = itemRequestRepository.findAllByOtherUsers(requestorId, pageable);
        assertEquals(1, requests.getContent().size());
        assertEquals(otherRequestor, requests.getContent().get(0).getRequestor().getId());

        requests = itemRequestRepository.findAllByOtherUsers(requestorNotFound, pageable);
        assertEquals(2, requests.getContent().size());
    }
}