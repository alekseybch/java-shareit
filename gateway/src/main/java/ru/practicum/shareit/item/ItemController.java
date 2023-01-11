package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemPatchDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> getItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @PositiveOrZero @RequestParam(value = "from",
                                                   defaultValue = "0") Integer from,
                                           @Positive @RequestParam(value = "size",
                                                   defaultValue = "20") Integer size) {
        log.info("request to get items userId = {}, from = {}, size = {}", userId, from, size);
        return itemClient.getItems(userId, from, size);
    }

    @GetMapping(value = "/{itemId}")
    public ResponseEntity<Object> getItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @PathVariable Long itemId) {
        log.info("request to get item userId = {}", userId);
        return itemClient.getItem(itemId, userId);
    }

    @GetMapping(value = "/search")
    public ResponseEntity<Object> findItem(@RequestParam("text") String text,
                                           @PositiveOrZero @RequestParam(value = "from",
                                                   defaultValue = "0") Integer from,
                                           @Positive @RequestParam(value = "size",
                                                   defaultValue = "20") Integer size) {
        log.info("request to search items witch text {}, from = {}, size = {}", text, from, size);
        return itemClient.findItem(text, from, size);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> createItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestBody @Valid @NotNull ItemRequestDto itemDto) {
        log.info("request to creating item {}, userId = {}", itemDto, userId);
        return itemClient.createItem(userId, itemDto);
    }

    @PostMapping(value = "/{itemId}/comment", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> createComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @PathVariable Long itemId,
                                                @RequestBody @Valid @NotNull CommentRequestDto commentDto) {
        log.info("request to creating comment {}, userId = {}, itemId = {}", commentDto, userId, itemId);
        return itemClient.createComment(userId, itemId, commentDto);
    }

    @PatchMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable("id") Long itemId,
                                             @RequestBody @Valid @NotNull ItemPatchDto itemDto) {
        log.info("request to update item {} userId = {}, itemId = {}", itemDto, userId, itemId);
        return itemClient.updateItem(userId, itemId, itemDto);
    }
}
