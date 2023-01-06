package ru.practicum.shareit.request.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@ToString
public class ItemReqRequestDto {
    @NotBlank(message = "can't be null or blank.")
    @Size(max = 200, message = "must not be more than 200 characters.")
    private String description;
    private Long requestorId;
}
