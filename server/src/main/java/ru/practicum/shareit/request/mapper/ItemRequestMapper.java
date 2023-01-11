package ru.practicum.shareit.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.global.mapper.EntityMapper;
import ru.practicum.shareit.request.db.model.ItemRequest;
import ru.practicum.shareit.request.dto.ItemReqRequestDto;
import ru.practicum.shareit.request.dto.ItemReqResponseDto;
import ru.practicum.shareit.user.db.repository.UserRepository;

@Mapper(componentModel = "spring", uses = UserRepository.class)
public interface ItemRequestMapper {
    @Mapping(target = "requestor", source = "dto.requestorId", qualifiedBy = EntityMapper.class)
    ItemRequest toItemRequest(ItemReqRequestDto dto);

    ItemReqResponseDto toItemReqDto(ItemRequest itemRequest);
}