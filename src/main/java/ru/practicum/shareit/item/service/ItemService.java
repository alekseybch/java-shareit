package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemPatchDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;

import java.util.List;

public interface ItemService {
    List<ItemResponseDto> getItems(Long userId);

    ItemResponseDto getById(Long itemId);

    List<ItemResponseDto> getByText(String text);

    ItemResponseDto save(ItemRequestDto itemDto);

    ItemResponseDto change(Long userId, Long itemId, ItemPatchDto itemDto);
}
