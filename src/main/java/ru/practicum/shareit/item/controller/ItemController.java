package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.db.model.Item;
import ru.practicum.shareit.item.dto.ItemPatchDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final ItemMapper itemMapper;

    @GetMapping
    public List<ItemResponseDto> getItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        List<Item> items = itemService.getItems(userId);
        return items.stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @GetMapping(value = "/{itemId}")
    public ItemResponseDto findItemById(@PathVariable Long itemId) {
        return itemMapper.toItemDto(itemService.getById(itemId));
    }

    @GetMapping(value = "/search")
    public List<ItemResponseDto> findItemByText(@RequestParam String text) {
        List<Item> items = itemService.getByText(text);
        return items.stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ItemResponseDto saveItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @RequestBody @Valid @NotNull ItemRequestDto itemDto) {
        Item item = itemMapper.toItem(itemDto);
        return itemMapper.toItemDto(itemService.save(userId, item));
    }

    @PatchMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ItemResponseDto changeItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @PathVariable("id") Long itemId,
                                      @RequestBody @Valid @NotNull ItemPatchDto itemDto) {
        Item item = itemMapper.toItem(itemDto);
        return itemMapper.toItemDto(itemService.change(userId, itemId, item));
    }
}
