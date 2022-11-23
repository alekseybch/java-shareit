package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.db.model.Booking;

public interface BookingMapper {
    Booking mapToBooking(BookingRequestDto dto);
}
