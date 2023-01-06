package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
public class CommentRequestDto {
    @NotBlank
    @Size(max = 500, message = "must not be more than 500 characters.")
    private String text;
    private Long itemId;
    private Long authorId;
}
