package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.ItemPatchDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.db.model.Item;
import ru.practicum.shareit.item.dto.ItemResponseDto;

public interface ItemMapper {
    Item toItem(ItemRequestDto dto);

    Item toItem(ItemPatchDto dto);

    ItemResponseDto toItemDto(Item item);
}
