package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.global.mapper.EntityMapper;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.db.model.Item;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.user.db.repository.UserRepository;

@Mapper(componentModel = "spring", uses = UserRepository.class)
public interface ItemMapper {
    @Mapping(target = "owner", source = "dto.ownerId", qualifiedBy = EntityMapper.class)
    Item toItem(ItemRequestDto dto);

    ItemResponseDto toItemDto(Item item);
}
