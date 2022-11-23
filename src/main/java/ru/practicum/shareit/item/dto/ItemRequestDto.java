package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
public class ItemRequestDto {
    @NotBlank(message = "can't be null or blank.")
    @Size(max = 150, message = "must not be more than 150 characters.")
    private String name;
    @NotBlank(message = "can't be null or blank.")
    @Size(max = 200, message = "must not be more than 200 characters.")
    private String description;
    @NotNull(message = "can't be null.")
    private Boolean available;
}
