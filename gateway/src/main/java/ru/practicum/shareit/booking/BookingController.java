package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
@Slf4j
public class BookingController {
    private final BookingClient bookingClient;

    @GetMapping
    public ResponseEntity<Object> getBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @RequestParam(value = "state",
                                                      defaultValue = "ALL") String stateParam,
                                              @PositiveOrZero @RequestParam(value = "from",
                                                      defaultValue = "0") Integer from,
                                              @Positive @RequestParam(value = "size",
                                                      defaultValue = "20") Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("request to get bookings with state {}, userId = {}, from = {}, size = {}", stateParam, userId, from, size);
        return bookingClient.getBookings(userId, state, from, size);
    }

    @GetMapping(value = "/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable Long bookingId) {
        log.info("request to get booking {}, userId = {}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping(value = "/owner")
    public ResponseEntity<Object> getItemOwnerBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                       @RequestParam(value = "state",
                                                         defaultValue = "ALL") String stateParam,
                                                       @PositiveOrZero @RequestParam(value = "from",
                                                         defaultValue = "0") Integer from,
                                                       @Positive @RequestParam(value = "size",
                                                         defaultValue = "20") Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("request to get user bookings with state {}, userId = {}, from = {}, size = {}", stateParam, userId, from, size);
        return bookingClient.getItemOwnerBookings(userId, state, from, size);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> bookItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @RequestBody @Valid @NotNull BookingRequestDto bookingDto) {
        log.info("request to creating booking {}, userId = {}", bookingDto, userId);
        return bookingClient.bookItem(userId, bookingDto);
    }

    @PatchMapping(value = "/{bookingId}")
    public ResponseEntity<Object> updateBookingStatus(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                      @PathVariable("bookingId") Long bookingId,
                                                      @RequestParam Boolean approved) {
        log.info("request to approve booking status {}, userId={}, approve={}", bookingId, userId, approved);
        return bookingClient.updateBookingStatus(userId, bookingId, approved);
    }
}
