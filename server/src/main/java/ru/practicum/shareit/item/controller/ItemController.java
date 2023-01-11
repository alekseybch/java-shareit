package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<ItemResponseDto> getItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @RequestParam(value = "from") Integer from,
                                          @RequestParam(value = "size") Integer size) {
        return itemService.getItems(userId, from, size);
    }

    @GetMapping(value = "/{itemId}")
    public ItemResponseDto getItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                   @PathVariable Long itemId) {
        return itemService.getItem(itemId, userId);
    }

    @GetMapping(value = "/search")
    public List<ItemResponseDto> findItem(@RequestParam("text") String text,
                                          @RequestParam(value = "from") Integer from,
                                          @RequestParam(value = "size") Integer size) {
        return itemService.findItem(text, from, size);
    }

    @PostMapping
    public ItemResponseDto createItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @RequestBody ItemRequestDto itemDto) {
        itemDto.setOwnerId(userId);
        return itemService.createItem(itemDto);
    }

    @PostMapping(value = "/{itemId}/comment")
    public CommentResponseDto createComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @PathVariable Long itemId,
                                            @RequestBody CommentRequestDto commentDto) {
        commentDto.setAuthorId(userId);
        commentDto.setItemId(itemId);
        return itemService.createComment(commentDto);
    }

    @PatchMapping(value = "/{id}")
    public ItemResponseDto updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @PathVariable("id") Long itemId,
                                      @RequestBody ItemPatchDto itemDto) {
        return itemService.updateItem(userId, itemId, itemDto);
    }
}
