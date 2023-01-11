package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemReqRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @GetMapping
    public ResponseEntity<Object> getItemRequests(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                  @PositiveOrZero @RequestParam(value = "from",
                                                          defaultValue = "0") Integer from,
                                                  @Positive @RequestParam(value = "size",
                                                          defaultValue = "20") Integer size) {
        log.info("request to get item requests userId = {}, from = {}, size = {}", userId, from, size);
        return itemRequestClient.getItemRequests(userId, from, size);
    }

    @GetMapping(value = "/{requestId}")
    public ResponseEntity<Object> getItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @PathVariable Long requestId) {
        log.info("request to get item request userId = {}", userId);
        return itemRequestClient.getItemRequest(requestId, userId);
    }

    @GetMapping(value = "/all")
    public ResponseEntity<Object> getOtherUsersItemRequests(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                            @PositiveOrZero @RequestParam(value = "from",
                                                                    defaultValue = "0") Integer from,
                                                            @Positive @RequestParam(value = "size",
                                                                    defaultValue = "20") Integer size) {
        log.info("request to get all other users item requests userId = {}, from = {}, size = {}", userId, from, size);
        return itemRequestClient.getOtherUsersItemRequests(userId, from, size);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> createItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @RequestBody @Valid @NotNull ItemReqRequestDto itemReqDto) {
        log.info("request to creating item request {}, userId = {}", itemReqDto, userId);
        return itemRequestClient.createItemRequest(itemReqDto, userId);
    }
}
