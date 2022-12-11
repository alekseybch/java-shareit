package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemPatchDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
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
    public List<ItemResponseDto> getItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getItems(userId);
    }

    @GetMapping(value = "/{itemId}")
    public ItemResponseDto findItemById(@PathVariable Long itemId) {
        return itemService.getById(itemId);
    }

    @GetMapping(value = "/search")
    public List<ItemResponseDto> findItemByText(@RequestParam String text) {
        return itemService.getByText(text);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ItemResponseDto saveItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @RequestBody @Valid @NotNull ItemRequestDto itemDto) {
        itemDto.setOwnerId(userId);
        return itemService.save(itemDto);
    }

    @PatchMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ItemResponseDto changeItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @PathVariable("id") Long itemId,
                                      @RequestBody @Valid @NotNull ItemPatchDto itemDto) {
        return itemService.change(userId, itemId, itemDto);
    }
}
