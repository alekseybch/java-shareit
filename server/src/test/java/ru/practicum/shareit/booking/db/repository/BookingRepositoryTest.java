package ru.practicum.shareit.booking.db.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.db.model.Booking;
import ru.practicum.shareit.booking.db.model.enums.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BookingRepositoryTest {
    @Autowired
    private BookingRepository bookingRepository;

    private final Long user1 = 1L;
    private final Long user2 = 2L;
    private final Long user3 = 3L;
    private final Long userNotFound = 4L;
    private final Long item1 = 1L;
    private final Long item2 = 2L;
    private final LocalDateTime time = LocalDateTime.now();
    private final Pageable pageable = PageRequest.of(0, 10, Sort.Direction.DESC, "start");

    @Test
    void findAllByBookerId_whenBookerIdFound_thenReturnedBookings() {
        Page<Booking> bookings = bookingRepository.findAllByBookerId(user1, pageable);
        assertEquals(1, bookings.getContent().size());
        assertEquals(user1, bookings.getContent().get(0).getBooker().getId());

        bookings = bookingRepository.findAllByBookerId(userNotFound, pageable);
        assertEquals(0, bookings.getContent().size());
    }

    @Test
    void findBookingsByBookerIdAndEndIsBefore_whenBookerIdFoundAndEndDateIsBeforeNow_thenReturnedBookings() {
        Page<Booking> bookings = bookingRepository.findBookingsByBookerIdAndEndIsBefore(user1, time, pageable);
        assertEquals(1, bookings.getContent().size());
        assertEquals(user1, bookings.getContent().get(0).getBooker().getId());

        bookings = bookingRepository.findBookingsByBookerIdAndEndIsBefore(user3, time, pageable);
        assertEquals(0, bookings.getContent().size());
    }

    @Test
    void findBookingsByBookerIdAndStartAfter_whenBookerIdFoundAndStartDateIsAfterNow_thenReturnedBookings() {
        Page<Booking> bookings = bookingRepository.findBookingsByBookerIdAndStartAfter(user3, time, pageable);
        assertEquals(1, bookings.getContent().size());
        assertEquals(user3, bookings.getContent().get(0).getBooker().getId());

        bookings = bookingRepository.findBookingsByBookerIdAndStartAfter(user1, time, pageable);
        assertEquals(0, bookings.getContent().size());
    }

    @Test
    void findBookingsByBookerIdAndStatus_whenBookerIdFoundAndStatusFound_thenReturnedBookings() {
        Page<Booking> bookings = bookingRepository.findBookingsByBookerIdAndStatus(user3, BookingStatus.APPROVED, pageable);
        assertEquals(1, bookings.getContent().size());
        assertEquals(user3, bookings.getContent().get(0).getBooker().getId());

        bookings = bookingRepository.findBookingsByBookerIdAndStatus(user2, BookingStatus.WAITING, pageable);
        assertEquals(1, bookings.getContent().size());
        assertEquals(user2, bookings.getContent().get(0).getBooker().getId());
    }

    @Test
    void findCurrent_whenBookingsCurrentNow_thenReturnedBookings() {
        Page<Booking> bookings = bookingRepository.findCurrent(user3, time, pageable);
        assertEquals(1, bookings.getContent().size());
        assertEquals(user3, bookings.getContent().get(0).getBooker().getId());

        bookings = bookingRepository.findCurrent(user1, time, pageable);
        assertEquals(0, bookings.getContent().size());
    }

    @Test
    void findByOwnerId_whenOwnerIdFound_thenReturnedBookings() {
        Page<Booking> bookings = bookingRepository.findByOwnerId(user1, pageable);
        assertEquals(2, bookings.getContent().size());
        assertEquals(user1, bookings.getContent().get(0).getItem().getOwner().getId());

        bookings = bookingRepository.findByOwnerId(userNotFound, pageable);
        assertEquals(0, bookings.getContent().size());
    }

    @Test
    void findCurrentByOwnerId_whenOwnerIdFoundAndBookingsCurrentNow_thenReturnedBookings() {
        Page<Booking> bookings = bookingRepository.findCurrentByOwnerId(user2, time, pageable);
        assertEquals(1, bookings.getContent().size());
        assertEquals(user2, bookings.getContent().get(0).getItem().getOwner().getId());

        bookings = bookingRepository.findCurrentByOwnerId(user1, time, pageable);
        assertEquals(0, bookings.getContent().size());
    }

    @Test
    void findPastByOwnerId_whenOwnerIdFoundAndBookingsPastNow_thenReturnedBookings() {
        Page<Booking> bookings = bookingRepository.findPastByOwnerId(user1, time, pageable);
        assertEquals(1, bookings.getContent().size());
        assertEquals(user1, bookings.getContent().get(0).getItem().getOwner().getId());

        bookings = bookingRepository.findPastByOwnerId(user3, time, pageable);
        assertEquals(0, bookings.getContent().size());
    }

    @Test
    void findFutureByOwnerId_whenOwnerIdFoundAndBookingsFutureNow_thenReturnedBookings() {
        Page<Booking> bookings = bookingRepository.findFutureByOwnerId(user1, time, pageable);
        assertEquals(1, bookings.getContent().size());
        assertEquals(user1, bookings.getContent().get(0).getItem().getOwner().getId());

        bookings = bookingRepository.findFutureByOwnerId(user2, time, pageable);
        assertEquals(0, bookings.getContent().size());
    }

    @Test
    void findByOwnerIdAndByStatus_whenOwnerIdFoundAndStatusFound_thenReturnedBookings() {
        Page<Booking> bookings = bookingRepository.findByOwnerIdAndByStatus(user1, BookingStatus.APPROVED, pageable);
        assertEquals(2, bookings.getContent().size());
        assertEquals(user1, bookings.getContent().get(0).getItem().getOwner().getId());

        bookings = bookingRepository.findByOwnerIdAndByStatus(user3, BookingStatus.WAITING, pageable);
        assertEquals(1, bookings.getContent().size());
        assertEquals(user3, bookings.getContent().get(0).getItem().getOwner().getId());
    }

    @Test
    void findLastAndNextById_whenItemIdFound_thenReturnedLastAndNextBookings() {
        List<Booking> bookings = bookingRepository.findLastAndNextById(item1, time);
        assertEquals(2, bookings.size());
        assertEquals(item1, bookings.get(0).getItem().getId());
        assertTrue(bookings.get(1).getStart().isBefore(time));
        assertTrue(bookings.get(0).getStart().isAfter(time));

        bookings = bookingRepository.findLastAndNextById(item2, time);
        assertEquals(1, bookings.size());
        assertEquals(item2, bookings.get(0).getItem().getId());
        assertTrue(bookings.get(0).getStart().isBefore(time));
    }

    @Test
    void findLastAndNextByIdList_whenItemIdFound_thenReturnedLastAndNextBookings() {
        List<Long> itemIds = List.of(item1, item2);

        List<Booking> bookings = bookingRepository.findLastAndNextByIdList(itemIds, time);
        assertEquals(3, bookings.size());
        assertEquals(item1, bookings.get(0).getItem().getId());
        assertEquals(item1, bookings.get(1).getItem().getId());
        assertEquals(item2, bookings.get(2).getItem().getId());
        assertTrue(bookings.get(1).getStart().isBefore(time));
        assertTrue(bookings.get(0).getStart().isAfter(time));
        assertTrue(bookings.get(2).getStart().isBefore(time));
    }

    @Test
    void findFreeInterval_whenItemFoundAndIntervalFree_thenReturnedEmptyList() {
        List<Booking> bookings = bookingRepository.findFreeInterval(item2, LocalDateTime.now(),
                LocalDateTime.now().plusDays(1));
        assertEquals(0, bookings.size());
    }

    @Test
    void findFreeInterval_whenItemFoundAndIntervalTaken_thenReturnedEmptyList() {
        List<Booking> bookings = bookingRepository.findFreeInterval(item1, LocalDateTime.now(),
                LocalDateTime.now().plusDays(3));
        assertEquals(1, bookings.size());
    }

    @Test
    void findByItemIdAndBookerIdAndStatusApproved_whenItemFoundAndBookerFoundAndStatusApproved_thenReturnedBooking() {
        Booking booking = bookingRepository.findByItemIdAndBookerIdAndStatusApproved(item1, user2, time);
        assertEquals(item1, booking.getItem().getId());
    }
}