package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.dto.BookingBookerDto;
import ru.practicum.shareit.user.dto.UserResponseDto;

import java.util.List;

@Getter
@Setter
public class ItemResponseDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private UserResponseDto owner;
    private BookingBookerDto lastBooking;
    private BookingBookerDto nextBooking;
    private List<CommentResponseDto> comments;
}
