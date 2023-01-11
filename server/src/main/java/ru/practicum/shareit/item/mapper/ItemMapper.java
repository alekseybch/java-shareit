package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.global.mapper.EntityMapper;
import ru.practicum.shareit.item.dto.ItemForRequestResponseDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.db.model.Item;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.request.db.repository.ItemRequestRepository;
import ru.practicum.shareit.user.db.repository.UserRepository;

@Mapper(componentModel = "spring", uses = {UserRepository.class, ItemRequestRepository.class})
public interface ItemMapper {
    @Mapping(target = "owner", source = "dto.ownerId", qualifiedBy = EntityMapper.class)
    @Mapping(target = "request", source = "dto.requestId", qualifiedBy = EntityMapper.class)
    Item toItem(ItemRequestDto dto);

    @Mapping(target = "requestId", source = "request.id")
    ItemResponseDto toItemDto(Item item);

    @Mapping(target = "ownerId", source = "owner.id")
    @Mapping(target = "requestId", source = "request.id")
    @Mapping(target = "available", source = "available")
    ItemForRequestResponseDto toItemForRequestDto(Item item);
}
