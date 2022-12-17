package ru.practicum.shareit.booking.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookingBookerDto {
    private Long id;
    private Long itemId;
    private Long bookerId;
}
