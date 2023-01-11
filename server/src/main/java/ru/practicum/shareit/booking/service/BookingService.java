package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.BookingState;

import java.util.List;

public interface BookingService {
    List<BookingResponseDto> getBookings(Long userId, BookingState state, Integer from, Integer size);

    BookingResponseDto getBooking(Long userId, Long bookingId);

    List<BookingResponseDto> getItemOwnerBookings(Long userId, BookingState state, Integer from, Integer size);

    BookingResponseDto bookItem(BookingRequestDto bookingDto);

    BookingResponseDto updateBookingStatus(Long userId, Long bookingId, Boolean approved);
}
