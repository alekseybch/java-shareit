package ru.practicum.shareit.request.service.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemReqRequestDto;
import ru.practicum.shareit.request.dto.ItemReqResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ItemRequestServiceImplIntTest {
    @Autowired
    private ItemRequestService itemRequestService;

    @Test
    void getItemRequests() {
        List<ItemReqResponseDto> itemRequests = itemRequestService.getItemRequests(1L, 1, 10);

        assertEquals(itemRequests.size(), 1);
        assertEquals(itemRequests.get(0).getId(), 1L);
    }

    @Test
    void getById() {
        ItemReqResponseDto itemReqResponseDto = itemRequestService.getById(1L, 1L);

        assertNotNull(itemReqResponseDto);
        assertEquals(itemReqResponseDto.getId(), 1L);
    }

    @Test
    void getAllByOtherRequestors() {
        List<ItemReqResponseDto> itemRequests = itemRequestService.getAllByOtherRequestors(2L, 1, 10);

        assertEquals(itemRequests.size(), 1);
        assertEquals(itemRequests.get(0).getId(), 1L);
    }

    @Test
    void save() {
        ItemReqRequestDto itemReqRequestDto = new ItemReqRequestDto();
        itemReqRequestDto.setRequestorId(1L);
        itemReqRequestDto.setDescription("need something");

        ItemReqResponseDto itemReqResponseDto = itemRequestService.save(itemReqRequestDto);

        assertNotNull(itemReqResponseDto);
        assertEquals(itemReqResponseDto.getId(), 3L);
        assertEquals(itemReqResponseDto.getDescription(), "need something");
    }
}