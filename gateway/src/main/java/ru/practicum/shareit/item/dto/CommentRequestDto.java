package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequestDto {
    @NotBlank
    @Size(max = 500, message = "must not be more than 500 characters.")
    private String text;
}
