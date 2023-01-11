package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemReqRequestDto;
import ru.practicum.shareit.request.dto.ItemReqResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @GetMapping
    public List<ItemReqResponseDto> getItemRequests(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @RequestParam(value = "from") Integer from,
                                                    @RequestParam(value = "size") Integer size) {
        return itemRequestService.getItemRequests(userId, from, size);
    }

    @GetMapping(value = "/{requestId}")
    public ItemReqResponseDto getItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable Long requestId) {
        return itemRequestService.getItemRequest(requestId, userId);
    }

    @GetMapping(value = "/all")
    public List<ItemReqResponseDto> getOtherUsersItemRequests(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                              @RequestParam(value = "from") Integer from,
                                                              @RequestParam(value = "size") Integer size) {
        return itemRequestService.getOtherUsersItemRequests(userId, from, size);
    }

    @PostMapping
    public ItemReqResponseDto createItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @RequestBody ItemReqRequestDto itemReqDto) {
        itemReqDto.setRequestorId(userId);
        return itemRequestService.createItemRequest(itemReqDto);
    }
}
