package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.dto.BookingState;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @GetMapping
    public List<BookingResponseDto> getBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @RequestParam(value = "state") BookingState state,
                                                @RequestParam(value = "from") Integer from,
                                                @RequestParam(value = "size") Integer size) {
        return bookingService.getBookings(userId, state, from, size);
    }

    @GetMapping(value = "/{bookingId}")
    public BookingResponseDto getBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @PathVariable Long bookingId) {
        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping(value = "/owner")
    public List<BookingResponseDto> getItemOwnerBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                         @RequestParam(value = "state") BookingState state,
                                                         @RequestParam(value = "from") Integer from,
                                                         @RequestParam(value = "size") Integer size) {
        return bookingService.getItemOwnerBookings(userId, state, from, size);
    }

    @PostMapping
    public BookingResponseDto bookItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                       @RequestBody BookingRequestDto bookingDto) {
        bookingDto.setBookerId(userId);
        return bookingService.bookItem(bookingDto);
    }

    @PatchMapping(value = "/{bookingId}")
    public BookingResponseDto updateBookingStatus(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                  @PathVariable("bookingId") Long bookingId,
                                                  @RequestParam Boolean approved) {
        return bookingService.updateBookingStatus(userId, bookingId, approved);
    }
}
