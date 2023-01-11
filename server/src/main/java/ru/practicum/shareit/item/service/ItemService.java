package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;

import java.util.List;

public interface ItemService {
    List<ItemResponseDto> getItems(Long userId, Integer from, Integer size);

    ItemResponseDto getItem(Long itemId, Long userId);

    List<ItemResponseDto> findItem(String text, Integer from, Integer size);

    ItemResponseDto createItem(ItemRequestDto itemDto);

    ItemResponseDto updateItem(Long userId, Long itemId, ItemPatchDto itemDto);

    CommentResponseDto createComment(CommentRequestDto commentDto);
}
