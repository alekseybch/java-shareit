package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @GetMapping(value = "/{bookingId}")
    public BookingResponseDto findBookingById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @PathVariable Long bookingId) {
        return bookingService.getById(userId, bookingId);
    }

    @GetMapping
    public List<BookingResponseDto> findBookingByState(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                       @RequestParam(value = "state", required = false,
                                                               defaultValue = "ALL") String state) {
        return bookingService.getAllByState(userId, state);
    }

    @GetMapping(value = "/owner")
    public List<BookingResponseDto> findBookingByStateForOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                               @RequestParam(value = "state", required = false,
                                                                       defaultValue = "ALL") String state) {
        return bookingService.getAllByOwner(userId, state);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponseDto saveBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @RequestBody @Valid @NotNull BookingRequestDto bookingDto) {
        bookingDto.setBookerId(userId);
        return bookingService.save(bookingDto);
    }

    @PatchMapping(value = "/{bookingId}")
    public BookingResponseDto changeBookingStatus(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                  @PathVariable("bookingId") Long bookingId,
                                                  @RequestParam Boolean approved) {
        return bookingService.changeStatus(userId, bookingId, approved);
    }
}
