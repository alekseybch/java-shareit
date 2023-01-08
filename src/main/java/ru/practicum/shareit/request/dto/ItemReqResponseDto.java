package ru.practicum.shareit.request.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.item.dto.ItemForRequestResponseDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ItemReqResponseDto {
    private Long id;
    private String description;
    private LocalDateTime created;
    private List<ItemForRequestResponseDto> items = new ArrayList<>();
}
