package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Size;

@Getter
@Setter
public class ItemPatchDto {
    @Size(max = 150, message = "must not be more than 150 characters.")
    private String name;
    @Size(max = 200, message = "must not be more than 200 characters.")
    private String description;
    private Boolean available;
    private Long request;
}
