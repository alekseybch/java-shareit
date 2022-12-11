package ru.practicum.shareit.item.mapper.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemPatchDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.db.model.Item;
import ru.practicum.shareit.user.db.model.User;
import ru.practicum.shareit.user.db.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class ItemMapperImpl implements ItemMapper {
    private final UserRepository userRepository;

    @Override
    public Item toItem(ItemRequestDto dto) {
        if (dto == null) {
            return null;
        }

        Item item = new Item();

        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        item.setAvailable(dto.getAvailable());

        User user = userRepository.readById(dto.getOwnerId())
                .orElseThrow(() -> new NotFoundException(String.format("user with id = %d not found.", dto.getOwnerId())));
        item.setOwner(user);

        return item;
    }

    @Override
    public Item toItem(ItemPatchDto dto) {
        if (dto == null) {
            return null;
        }

        Item item = new Item();

        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        item.setAvailable(dto.getAvailable());

        return item;
    }

    @Override
    public ItemResponseDto toItemDto(Item item) {
        if (item == null) {
            return null;
        }

        ItemResponseDto itemDto = new ItemResponseDto();

        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());

        return itemDto;
    }
}
