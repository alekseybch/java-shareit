package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ItemReqRequestDto {
    @NotBlank(message = "can't be null or blank.")
    @Size(max = 200, message = "must not be more than 200 characters.")
    private String description;
}
