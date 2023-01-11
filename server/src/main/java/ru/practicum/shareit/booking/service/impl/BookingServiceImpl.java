package ru.practicum.shareit.booking.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.db.model.Booking;
import ru.practicum.shareit.booking.db.model.enums.BookingStatus;
import ru.practicum.shareit.booking.db.repository.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.global.exception.*;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.global.utility.PageableConverter.getPageable;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final BookingMapper bookingMapper;

    @Override
    public BookingResponseDto getBooking(Long userId, Long bookingId) {
        log.info("get a booking with id = {}.", bookingId);
        Booking booking = bookingRepository.getReferenceById(bookingId);
        if ((!booking.getItem().getOwner().getId().equals(userId)) && (!booking.getBooker().getId().equals(userId))) {
            throw new NotItemOwnerException(String.format("user with id = %d does not own booking with id = %d.",
                    userId, booking.getId()));
        }
        return bookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingResponseDto> getBookings(Long userId, BookingState state, Integer from, Integer size) {
        log.info("get all bookings with state = {}.", state);
        userService.getUser(userId);
        LocalDateTime time = LocalDateTime.now();
        Page<Booking> bookings;
        Pageable pageable = getPageable(from, size, Sort.Direction.DESC, "start");
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByBookerId(userId, pageable);
                break;
            case CURRENT:
                bookings = bookingRepository.findCurrent(userId, time, pageable);
                break;
            case PAST:
                bookings = bookingRepository.findBookingsByBookerIdAndEndIsBefore(userId, time, pageable);
                break;
            case FUTURE:
                bookings = bookingRepository.findBookingsByBookerIdAndStartAfter(userId, time, pageable);
                break;
            case REJECTED:
                bookings = bookingRepository.findBookingsByBookerIdAndStatus(userId, BookingStatus.REJECTED, pageable);
                break;
            case WAITING:
                bookings = bookingRepository.findBookingsByBookerIdAndStatus(userId, BookingStatus.WAITING, pageable);
                break;
            default:
                throw new BadStateException(String.format("Unknown state: %s", state));
        }
        return bookings.stream()
                .map(bookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingResponseDto> getItemOwnerBookings(Long userId, BookingState state, Integer from, Integer size) {
        log.info("get item owner bookings with state = {}, userId={}.", state, userId);
        userService.getUser(userId);
        LocalDateTime time = LocalDateTime.now();
        Page<Booking> bookings;
        Pageable pageable = getPageable(from, size, Sort.Direction.DESC, "start");
        switch (state) {
            case ALL:
                bookings = bookingRepository.findByOwnerId(userId, pageable);
                break;
            case CURRENT:
                bookings = bookingRepository.findCurrentByOwnerId(userId, time, pageable);
                break;
            case PAST:
                bookings = bookingRepository.findPastByOwnerId(userId, time, pageable);
                break;
            case FUTURE:
                bookings = bookingRepository.findFutureByOwnerId(userId, time, pageable);
                break;
            case REJECTED:
                bookings = bookingRepository.findByOwnerIdAndByStatus(userId, BookingStatus.REJECTED, pageable);
                break;
            case WAITING:
                bookings = bookingRepository.findByOwnerIdAndByStatus(userId, BookingStatus.WAITING, pageable);
                break;
            default:
                throw new BadStateException(String.format("Unknown state: %s", state));
        }
        return bookings.stream()
                .map(bookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public BookingResponseDto bookItem(BookingRequestDto bookingDto) {
        log.info("save a booking {}.", bookingDto);
        if (bookingDto.getEnd().isBefore(bookingDto.getStart())) {
            throw new BadBookingDateException(String.format("end date (%s) should be later than start date (%s).",
                    bookingDto.getStart(), bookingDto.getEnd()));
        }
        Booking booking = bookingMapper.toBooking(bookingDto);
        if (booking.getItem() == null) {
            throw new NotFoundException(String.format("item with id = %d not found.", bookingDto.getItemId()));
        }
        if (booking.getBooker() == null) {
            throw new NotFoundException(String.format("user with id = %d not found.", bookingDto.getBookerId()));
        }
        if (!booking.getItem().getAvailable()) {
            throw new NotItemAvailableException(String.format("item with id = %d is not available for booking.",
                    booking.getItem().getId()));
        }
        if (booking.getItem().getOwner().getId().equals(booking.getBooker().getId())) {
            throw new NotItemOwnerException(String.format("user with id = %d cannot book their item.",
                    booking.getBooker().getId()));
        }
        if (!bookingRepository.findFreeInterval(booking.getItem().getId(), booking.getStart(), booking.getEnd())
                .isEmpty()) {
            throw new NotItemAvailableException("item cannot be booked for these dates.");
        }
        booking.setStatus(BookingStatus.WAITING);
        Booking savedBooking = bookingRepository.save(booking);
        log.info("booking with id = {} is saved {}.", savedBooking.getId(), savedBooking);
        return bookingMapper.toBookingDto(savedBooking);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public BookingResponseDto updateBookingStatus(Long userId, Long bookingId, Boolean isApproved) {
        log.info("update booking with id = {} status.", bookingId);
        Booking booking = bookingRepository.getReferenceById(bookingId);
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotItemOwnerException(String.format("user with id = %d does not own booking with id = %d.",
                    userId, booking.getId()));
        }
        if (booking.getStatus().equals(BookingStatus.APPROVED) ||
                booking.getStatus().equals(BookingStatus.REJECTED)) {
            throw new BadApproveStatusException("booking status has already been confirmed");
        }
        if (isApproved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        log.info("booking with id = {} status changed to {}.", bookingId, booking.getStatus());
        return bookingMapper.toBookingDto(bookingRepository.save(booking));
    }
}
