package ru.practicum.shareit.booking.service.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.db.model.enums.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.shareit.booking.dto.BookingState.ALL;

@SpringBootTest
@Transactional
class BookingServiceImplIntTest {
    @Autowired
    private BookingService bookingService;

    @Test
    void getBooking() {
        BookingResponseDto bookingResponseDto = bookingService.getBooking(3L, 1L);

        assertNotNull(bookingResponseDto);
        assertEquals(bookingResponseDto.getId(), 1L);
        assertEquals(bookingResponseDto.getBooker().getId(), 3L);
    }

    @Test
    void getBookings() {
        List<BookingResponseDto> bookings = bookingService.getBookings(1L, ALL, 1, 10);

        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0).getBooker().getId(), 1L);
    }

    @Test
    void getItemOwnerBookings() {
        List<BookingResponseDto> bookings = bookingService.getItemOwnerBookings(1L, ALL, 1, 10);

        assertEquals(bookings.size(), 2);
        assertEquals(bookings.get(0).getItem().getId(), 1L);
        assertEquals(bookings.get(1).getItem().getId(), 1L);
    }

    @Test
    void bookItem() {
        BookingRequestDto bookingRequestDto = new BookingRequestDto();
        bookingRequestDto.setStart(LocalDateTime.now());
        bookingRequestDto.setEnd(LocalDateTime.now().plusDays(1));
        bookingRequestDto.setItemId(3L);
        bookingRequestDto.setBookerId(2L);

        BookingResponseDto bookingResponseDto = bookingService.bookItem(bookingRequestDto);

        assertNotNull(bookingResponseDto);
        assertEquals(bookingResponseDto.getId(), 6L);
        assertEquals(bookingResponseDto.getItem().getId(), 3L);
        assertEquals(bookingResponseDto.getBooker().getId(), 2L);
    }

    @Test
    void updateBookingStatus() {
        BookingResponseDto bookingResponseDto = bookingService.updateBookingStatus(3L, 3L, true);

        assertNotNull(bookingResponseDto);
        assertEquals(bookingResponseDto.getStatus(), BookingStatus.APPROVED);
    }
}