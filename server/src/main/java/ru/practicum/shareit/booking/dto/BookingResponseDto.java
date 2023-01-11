package ru.practicum.shareit.booking.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.db.model.enums.BookingStatus;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.user.dto.UserResponseDto;

import java.time.LocalDateTime;

@Getter
@Setter
public class BookingResponseDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private ItemResponseDto item;
    private UserResponseDto booker;
    private BookingStatus status;
}
