package ru.practicum.shareit.item.service.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ItemServiceImplIntTest {
    @Autowired
    private ItemService itemService;

    @Test
    void getItems() {
        List<ItemResponseDto> items = itemService.getItems(1L, 1, 10);

        assertEquals(items.size(), 1);
        assertEquals(items.get(0).getOwner().getId(), 1L);
    }

    @Test
    void getById() {
        ItemResponseDto itemResponseDto = itemService.getById(1L, 1L);

        assertNotNull(itemResponseDto);
        assertEquals(itemResponseDto.getId(), 1L);
        assertEquals(itemResponseDto.getOwner().getId(), 1L);
    }

    @Test
    void getByText() {
        List<ItemResponseDto> items = itemService.getByText("dRiLL", 1, 10);

        assertEquals(items.size(), 1);
        assertEquals(items.get(0).getName(), "drill");
    }

    @Test
    void save() {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setOwnerId(1L);
        itemRequestDto.setName("new");
        itemRequestDto.setDescription("new description");
        itemRequestDto.setAvailable(true);

        ItemResponseDto itemResponseDto = itemService.save(itemRequestDto);

        assertNotNull(itemResponseDto);
        assertEquals(itemResponseDto.getId(), 4L);
        assertEquals(itemResponseDto.getName(), "new");
        assertEquals(itemResponseDto.getDescription(), "new description");
    }

    @Test
    void change() {
        ItemPatchDto itemPatchDto = new ItemPatchDto();
        itemPatchDto.setName("changed");

        ItemResponseDto itemResponseDto = itemService.change(1L, 1L, itemPatchDto);

        assertNotNull(itemResponseDto);
        assertEquals(itemResponseDto.getId(), 1L);
        assertEquals(itemResponseDto.getOwner().getId(), 1L);
        assertEquals(itemResponseDto.getName(), "changed");
    }

    @Test
    void saveComment() {
        CommentRequestDto commentRequestDto = new CommentRequestDto();
        commentRequestDto.setItemId(1L);
        commentRequestDto.setAuthorId(2L);
        commentRequestDto.setText("best");

        CommentResponseDto commentResponseDto = itemService.saveComment(commentRequestDto);

        assertNotNull(commentResponseDto);
        assertEquals(commentResponseDto.getText(), "best");
    }
}