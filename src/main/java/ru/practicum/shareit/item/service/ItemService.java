package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;

import java.util.List;

public interface ItemService {
    List<ItemResponseDto> getItems(Long userId, Integer from, Integer size);

    ItemResponseDto getById(Long itemId, Long userId);

    List<ItemResponseDto> getByText(String text, Integer from, Integer size);

    ItemResponseDto save(ItemRequestDto itemDto);

    ItemResponseDto change(Long userId, Long itemId, ItemPatchDto itemDto);

    CommentResponseDto saveComment(CommentRequestDto commentDto);
}
