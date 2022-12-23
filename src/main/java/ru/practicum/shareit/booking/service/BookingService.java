package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

public interface BookingService {
    BookingResponseDto getById(Long userId, Long bookingId);

    List<BookingResponseDto> getAllByState(Long userId, String state);

    List<BookingResponseDto> getAllByOwner(Long userId, String state);

    BookingResponseDto save(BookingRequestDto bookingDto);

    BookingResponseDto changeStatus(Long userId, Long bookingId, Boolean approved);
}
