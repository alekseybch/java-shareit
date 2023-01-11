package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemReqRequestDto;
import ru.practicum.shareit.request.dto.ItemReqResponseDto;

import java.util.List;

public interface ItemRequestService {
    List<ItemReqResponseDto> getItemRequests(Long userId, Integer from, Integer size);

    ItemReqResponseDto getItemRequest(Long requestId, Long userId);

    List<ItemReqResponseDto> getOtherUsersItemRequests(Long userId, Integer from, Integer size);

    ItemReqResponseDto createItemRequest(ItemReqRequestDto itemReqDto);
}
