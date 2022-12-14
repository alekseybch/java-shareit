package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<ItemResponseDto> findItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @RequestParam(value = "from", required = false,
                                                  defaultValue = "0") Integer from,
                                           @RequestParam(value = "size", required = false,
                                                  defaultValue = "20") Integer size) {
        return itemService.getItems(userId, from, size);
    }

    @GetMapping(value = "/{itemId}")
    public ItemResponseDto findItemById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                        @PathVariable Long itemId) {
        return itemService.getById(itemId, userId);
    }

    @GetMapping(value = "/search")
    public List<ItemResponseDto> findItemByText(@RequestParam("text") String text,
                                                @RequestParam(value = "from", required = false,
                                                        defaultValue = "0") Integer from,
                                                @RequestParam(value = "size", required = false,
                                                        defaultValue = "20") Integer size) {
        return itemService.getByText(text, from, size);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ItemResponseDto saveItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @RequestBody @Valid @NotNull ItemRequestDto itemDto) {
        itemDto.setOwnerId(userId);
        return itemService.save(itemDto);
    }

    @PostMapping(value = "/{itemId}/comment", consumes = MediaType.APPLICATION_JSON_VALUE)
    public CommentResponseDto createComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @PathVariable Long itemId,
                                            @RequestBody @Valid @NotNull CommentRequestDto commentDto) {
        commentDto.setAuthorId(userId);
        commentDto.setItemId(itemId);
        return itemService.saveComment(commentDto);
    }

    @PatchMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ItemResponseDto changeItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @PathVariable("id") Long itemId,
                                      @RequestBody @Valid @NotNull ItemPatchDto itemDto) {
        return itemService.change(userId, itemId, itemDto);
    }
}
