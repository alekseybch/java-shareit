package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemReqRequestDto;
import ru.practicum.shareit.request.dto.ItemReqResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @GetMapping
    public List<ItemReqResponseDto> findItemRequests(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                     @RequestParam(value = "from", required = false,
                                                             defaultValue = "0") Integer from,
                                                     @RequestParam(value = "size", required = false,
                                                             defaultValue = "20") Integer size) {
        return itemRequestService.getItemRequests(userId, from, size);
    }

    @GetMapping(value = "/{requestId}")
    public ItemReqResponseDto findItemRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                  @PathVariable Long requestId) {
        return itemRequestService.getById(requestId, userId);
    }

    @GetMapping(value = "/all")
    public List<ItemReqResponseDto> findItemRequestsByOtherRequestors(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                                      @RequestParam(value = "from", required = false,
                                                                              defaultValue = "0") Integer from,
                                                                      @RequestParam(value = "size", required = false,
                                                                              defaultValue = "20") Integer size) {
        return itemRequestService.getAllByOtherRequestors(userId, from, size);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ItemReqResponseDto saveItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @RequestBody @Valid @NotNull ItemReqRequestDto itemReqDto) {
        itemReqDto.setRequestorId(userId);
        return itemRequestService.save(itemReqDto);
    }
}
