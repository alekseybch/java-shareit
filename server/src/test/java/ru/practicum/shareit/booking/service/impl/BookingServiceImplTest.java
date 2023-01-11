package ru.practicum.shareit.booking.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import ru.practicum.shareit.booking.db.model.Booking;
import ru.practicum.shareit.booking.db.model.enums.BookingStatus;
import ru.practicum.shareit.booking.db.repository.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.global.exception.*;
import ru.practicum.shareit.item.db.model.Item;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.user.db.model.User;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.booking.dto.BookingState.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserService userService;
    @Mock
    private BookingMapper bookingMapper;
    @InjectMocks
    private BookingServiceImpl bookingService;

    private final User user1 = new User();
    private final User user2 = new User();
    private final Item item1 = new Item();
    private final Booking booking1 = new Booking();
    private final BookingRequestDto bookingRequestDto = new BookingRequestDto();
    private final Long userId = 1L;
    private final Long notOwnerId = 3L;
    private final Long userNotFound = -1L;
    private final Long bookingId = 1L;

    @BeforeEach
    void setUp() {
        bookingService = new BookingServiceImpl(bookingRepository, userService, bookingMapper);

        user1.setId(1L);
        user1.setName("user");
        user1.setEmail("user@user.com");

        user2.setId(2L);
        user2.setName("other");
        user2.setEmail("other@other.com");

        item1.setId(1L);
        item1.setName("drill");
        item1.setDescription("simple drill");
        item1.setAvailable(true);
        item1.setOwner(user1);

        booking1.setId(1L);
        booking1.setItem(item1);
        booking1.setBooker(user2);
        booking1.setStart(LocalDateTime.now());
        booking1.setEnd(booking1.getStart().plusDays(1));
        booking1.setStatus(BookingStatus.WAITING);

        bookingRequestDto.setItemId(item1.getId());
        bookingRequestDto.setBookerId(user2.getId());
        bookingRequestDto.setStart(LocalDateTime.now());
        bookingRequestDto.setEnd(bookingRequestDto.getStart().plusDays(1));
    }

    @Test
    void getBooking_whenUserFound_thenReturnedBooking() {
        Long ownerId = 2L;

        when(bookingRepository.getReferenceById(anyLong())).thenReturn(booking1);
        when(bookingMapper.toBookingDto(any())).thenReturn(toBookingDto(booking1));

        BookingResponseDto actualBooking = bookingService.getBooking(ownerId, bookingId);

        verify(bookingRepository, times(1)).getReferenceById(anyLong());
        verify(bookingMapper, times(1)).toBookingDto(any());
        assertEquals(actualBooking.getId(), booking1.getId());
    }

    @Test
    void getBooking_whenUserNotFound_thenEntityNotFoundException() {
        when(bookingRepository.getReferenceById(anyLong())).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> bookingService.getBooking(userNotFound, bookingId));
    }

    @Test
    void getBooking_whenUserNotOwner_thenNotOwnerException() {
        when(bookingRepository.getReferenceById(anyLong())).thenReturn(booking1);

        assertThrows(NotItemOwnerException.class, () -> bookingService.getBooking(notOwnerId, bookingId));
    }

    @Test
    void getBookings_whenStateDefault_thenReturnedBookings() {
        when(userService.getUser(anyLong())).thenReturn(toUserDto(user1));
        when(bookingRepository.findAllByBookerId(anyLong(), any())).thenReturn(Page.empty());

        bookingService.getBookings(userId, ALL, 0, 10);

        verify(bookingRepository, times(1)).findAllByBookerId(anyLong(), any());
    }

    @Test
    void getBookings_whenStateCurrent_thenReturnedBookings() {
        when(userService.getUser(anyLong())).thenReturn(toUserDto(user1));
        when(bookingRepository.findCurrent(anyLong(), any(), any())).thenReturn(Page.empty());

        bookingService.getBookings(userId, CURRENT, 0, 10);

        verify(bookingRepository, times(1)).findCurrent(anyLong(), any(), any());
    }

    @Test
    void getBookings_whenStatePast_thenReturnedBookings() {
        when(userService.getUser(anyLong())).thenReturn(toUserDto(user1));
        when(bookingRepository.findBookingsByBookerIdAndEndIsBefore(anyLong(), any(), any())).thenReturn(Page.empty());

        bookingService.getBookings(userId, PAST, 0, 10);

        verify(bookingRepository, times(1)).findBookingsByBookerIdAndEndIsBefore(anyLong(), any(), any());
    }

    @Test
    void getBookings_whenStateFuture_thenReturnedBookings() {
        when(userService.getUser(anyLong())).thenReturn(toUserDto(user1));
        when(bookingRepository.findBookingsByBookerIdAndStartAfter(anyLong(), any(), any())).thenReturn(Page.empty());

        bookingService.getBookings(userId, FUTURE, 0, 10);

        verify(bookingRepository, times(1)).findBookingsByBookerIdAndStartAfter(anyLong(), any(), any());
    }

    @Test
    void getBookings_whenStateRejected_thenReturnedBookings() {
        when(userService.getUser(anyLong())).thenReturn(toUserDto(user1));
        when(bookingRepository.findBookingsByBookerIdAndStatus(anyLong(), any(), any())).thenReturn(Page.empty());

        bookingService.getBookings(userId, REJECTED, 0, 10);

        verify(bookingRepository, times(1)).findBookingsByBookerIdAndStatus(anyLong(), any(), any());
    }

    @Test
    void getBookings_whenStateWaiting_thenReturnedBookings() {
        when(userService.getUser(anyLong())).thenReturn(toUserDto(user1));
        when(bookingRepository.findBookingsByBookerIdAndStatus(anyLong(), any(), any())).thenReturn(Page.empty());

        bookingService.getBookings(userId, WAITING, 0, 10);

        verify(bookingRepository, times(1)).findBookingsByBookerIdAndStatus(anyLong(), any(), any());
    }

    @Test
    void getItemOwnerBookings_whenStateDefault_thenReturnedBookings() {
        when(userService.getUser(anyLong())).thenReturn(toUserDto(user1));
        when(bookingRepository.findByOwnerId(anyLong(), any())).thenReturn(Page.empty());

        bookingService.getItemOwnerBookings(userId, ALL, 0, 10);

        verify(bookingRepository, times(1)).findByOwnerId(anyLong(), any());
    }

    @Test
    void getItemOwnerBookings_whenStateCurrent_thenReturnedBookings() {
        when(userService.getUser(anyLong())).thenReturn(toUserDto(user1));
        when(bookingRepository.findCurrentByOwnerId(anyLong(), any(), any())).thenReturn(Page.empty());

        bookingService.getItemOwnerBookings(userId, CURRENT, 0, 10);

        verify(bookingRepository, times(1)).findCurrentByOwnerId(anyLong(), any(), any());
    }

    @Test
    void getItemOwnerBookings_whenStatePast_thenReturnedBookings() {
        when(userService.getUser(anyLong())).thenReturn(toUserDto(user1));
        when(bookingRepository.findPastByOwnerId(anyLong(), any(), any())).thenReturn(Page.empty());

        bookingService.getItemOwnerBookings(userId, PAST, 0, 10);

        verify(bookingRepository, times(1)).findPastByOwnerId(anyLong(), any(), any());
    }

    @Test
    void getItemOwnerBookings_whenStateFuture_thenReturnedBookings() {
        when(userService.getUser(anyLong())).thenReturn(toUserDto(user1));
        when(bookingRepository.findFutureByOwnerId(anyLong(), any(), any())).thenReturn(Page.empty());

        bookingService.getItemOwnerBookings(userId, FUTURE, 0, 10);

        verify(bookingRepository, times(1)).findFutureByOwnerId(anyLong(), any(), any());
    }

    @Test
    void getItemOwnerBookings_whenStateRejected_thenReturnedBookings() {
        when(userService.getUser(anyLong())).thenReturn(toUserDto(user1));
        when(bookingRepository.findByOwnerIdAndByStatus(anyLong(), any(), any())).thenReturn(Page.empty());

        bookingService.getItemOwnerBookings(userId, REJECTED, 0, 10);

        verify(bookingRepository, times(1)).findByOwnerIdAndByStatus(anyLong(), any(), any());
    }

    @Test
    void getItemOwnerBookings_whenStateWaiting_thenReturnedBookings() {
        when(userService.getUser(anyLong())).thenReturn(toUserDto(user1));
        when(bookingRepository.findByOwnerIdAndByStatus(anyLong(), any(), any())).thenReturn(Page.empty());

        bookingService.getItemOwnerBookings(userId, WAITING, 0, 10);

        verify(bookingRepository, times(1)).findByOwnerIdAndByStatus(anyLong(), any(), any());
    }

    @Test
    void bookItem_whenInvoked_thenSaveBooking() {
        when(bookingMapper.toBooking(any())).thenReturn(booking1);
        when(bookingRepository.findFreeInterval(anyLong(), any(), any())).thenReturn(Collections.emptyList());
        when(bookingRepository.save(any())).thenReturn(booking1);
        when(bookingMapper.toBookingDto(any())).thenReturn(toBookingDto(booking1));

        BookingResponseDto savedBooking = bookingService.bookItem(bookingRequestDto);

        verify(bookingMapper, times(1)).toBooking(any());
        verify(bookingRepository, times(1)).findFreeInterval(anyLong(), any(), any());
        verify(bookingRepository, times(1)).save(any());
        verify(bookingMapper, times(1)).toBookingDto(any());
        assertEquals(savedBooking.getId(), booking1.getId());
    }

    @Test
    void bookItem_whenIncorrectEndDate_thenBadBookingDateException() {
        bookingRequestDto.setEnd(booking1.getEnd().minusDays(3));

        assertThrows(BadBookingDateException.class, () -> bookingService.bookItem(bookingRequestDto));
    }

    @Test
    void bookItem_whenItemNotFound_thenNotFoundException() {
        bookingRequestDto.setItemId(null);
        booking1.setItem(null);

        when(bookingMapper.toBooking(any())).thenReturn(booking1);

        assertThrows(NotFoundException.class, () -> bookingService.bookItem(bookingRequestDto));
    }

    @Test
    void bookItem_whenBookerNotFound_thenNotFoundException() {
        bookingRequestDto.setBookerId(null);
        booking1.setBooker(null);

        when(bookingMapper.toBooking(any())).thenReturn(booking1);

        assertThrows(NotFoundException.class, () -> bookingService.bookItem(bookingRequestDto));
    }

    @Test
    void bookItem_whenItemNotAvailable_thenNotItemAvailableException() {
        item1.setAvailable(false);

        when(bookingMapper.toBooking(any())).thenReturn(booking1);

        assertThrows(NotItemAvailableException.class, () -> bookingService.bookItem(bookingRequestDto));
    }

    @Test
    void bookItem_whenItemBooked_thenNotItemAvailableException() {
        List<Booking> bookings = List.of(new Booking());

        when(bookingMapper.toBooking(any())).thenReturn(booking1);
        when(bookingRepository.findFreeInterval(anyLong(), any(), any())).thenReturn(bookings);

        assertThrows(NotItemAvailableException.class, () -> bookingService.bookItem(bookingRequestDto));
    }

    @Test
    void bookItem_whenUserIsItemOwner_thenNotItemOwnerException() {
        booking1.setBooker(user1);

        when(bookingMapper.toBooking(any())).thenReturn(booking1);

        assertThrows(NotItemOwnerException.class, () -> bookingService.bookItem(bookingRequestDto));
    }

    @Test
    void updateBookingStatus_whenInvoked_thenChangeStatus() {
        when(bookingRepository.getReferenceById(anyLong())).thenReturn(booking1);
        when(bookingRepository.save(any())).thenReturn(booking1);

        bookingService.updateBookingStatus(userId, bookingId, true);

        verify(bookingRepository, times(1)).getReferenceById(anyLong());
        verify(bookingRepository, times(1)).save(any());
        verify(bookingMapper, times(1)).toBookingDto(any());
        assertEquals(booking1.getStatus(), BookingStatus.APPROVED);
    }

    @Test
    void updateBookingStatus_whenUserNotItemOwner_thenNotItemOwnerException() {
        when(bookingRepository.getReferenceById(anyLong())).thenReturn(booking1);

        assertThrows(NotItemOwnerException.class, () -> bookingService.updateBookingStatus(notOwnerId, bookingId, true));
    }

    @Test
    void updateBookingStatus_whenStatusAlreadyChanged_thenBadApproveStatusException() {
        booking1.setStatus(BookingStatus.APPROVED);

        when(bookingRepository.getReferenceById(anyLong())).thenReturn(booking1);

        assertThrows(BadApproveStatusException.class, () -> bookingService.updateBookingStatus(userId, bookingId, true));
    }

    private UserResponseDto toUserDto(User user) {
        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setId(user.getId());
        userResponseDto.setName(user.getName());
        userResponseDto.setEmail(user.getEmail());
        return userResponseDto;
    }

    private ItemResponseDto toItemDto(Item item) {
        ItemResponseDto itemResponseDto = new ItemResponseDto();
        itemResponseDto.setId(item.getId());
        itemResponseDto.setName(item.getName());
        itemResponseDto.setDescription(item.getDescription());
        itemResponseDto.setAvailable(item.getAvailable());
        itemResponseDto.setOwner(toUserDto(item.getOwner()));
        return itemResponseDto;
    }

    private BookingResponseDto toBookingDto(Booking booking) {
        BookingResponseDto bookingResponseDto = new BookingResponseDto();
        bookingResponseDto.setId(booking.getId());
        bookingResponseDto.setStart(booking.getStart());
        bookingResponseDto.setEnd(booking.getEnd());
        bookingResponseDto.setItem(toItemDto(booking.getItem()));
        bookingResponseDto.setBooker(toUserDto(booking.getBooker()));
        bookingResponseDto.setStatus(booking.getStatus());
        return bookingResponseDto;
    }
}