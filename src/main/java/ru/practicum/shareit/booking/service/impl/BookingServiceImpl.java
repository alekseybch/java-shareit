package ru.practicum.shareit.booking.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import ru.practicum.shareit.global.exception.*;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final BookingMapper bookingMapper;

    @Override
    @Transactional(readOnly = true)
    public BookingResponseDto getById(Long userId, Long bookingId) {
        log.info("request to get a booking with id = {}.", bookingId);
        Booking booking = Optional.of(bookingRepository.getReferenceById(bookingId))
                .orElseThrow(() -> new NotFoundException(String.format("booking with id = %d not found.", bookingId)));
        if ((!booking.getItem().getOwner().getId().equals(userId)) && (!booking.getBooker().getId().equals(userId))) {
            throw new NotItemOwnerException(String.format("user with id = %d does not own booking with id = %d.",
                    userId, booking.getId()));
        }
        return bookingMapper.toBookingDto(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponseDto> getAllByState(Long userId, String state) {
        log.info("request to get all bookings by state = {}.", state);
        userService.getById(userId);
        LocalDateTime time = LocalDateTime.now();
        List<Booking> bookings;
        Sort sort = Sort.by(Sort.Order.desc("start"));
        switch (state) {
            case "ALL":
                bookings = bookingRepository.findAllByBookerId(userId, Sort.by(Sort.Order.desc("start")));
                break;
            case "CURRENT":
                bookings = bookingRepository.findCurrent(userId, time);
                break;
            case "PAST":
                bookings = bookingRepository.findBookingsByBookerIdAndEndIsBefore(userId, time, sort);
                break;
            case "FUTURE":
                bookings = bookingRepository.findBookingsByBookerIdAndStartAfter(userId, time, sort);
                break;
            case "REJECTED":
                bookings = bookingRepository.findBookingsByBookerIdAndStatus(userId, BookingStatus.REJECTED, sort);
                break;
            case "WAITING":
                bookings = bookingRepository.findBookingsByBookerIdAndStatus(userId, BookingStatus.WAITING, sort);
                break;
            default:
                throw new BadStateException(String.format("Unknown state: %s", state));
        }
        return bookings.stream()
                .map(bookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponseDto> getAllByOwner(Long userId, String state) {
        log.info("request to receive all bookings by state = {} from item owner = {}.", state, userId);
        userService.getById(userId);
        LocalDateTime time = LocalDateTime.now();
        List<Booking> bookings;
        switch (state) {
            case "ALL":
                bookings = bookingRepository.findByOwnerId(userId);
                break;
            case "CURRENT":
                bookings = bookingRepository.findCurrentByOwnerId(userId, time);
                break;
            case "PAST":
                bookings = bookingRepository.findPastByOwnerId(userId, time);
                break;
            case "FUTURE":
                bookings = bookingRepository.findFutureByOwnerId(userId, time);
                break;
            case "REJECTED":
                bookings = bookingRepository.findByOwnerIdAndByStatus(userId, BookingStatus.REJECTED);
                break;
            case "WAITING":
                bookings = bookingRepository.findByOwnerIdAndByStatus(userId, BookingStatus.WAITING);
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
    public BookingResponseDto save(BookingRequestDto bookingDto) {
        log.info("request to save a booking {}.", bookingDto);
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
        if (!bookingRepository.findFreeInterval(booking.getItem().getId(), booking.getStart(), booking.getEnd())
                .isEmpty()) {
            throw new NotItemAvailableException("item cannot be booked for these dates.");
        }
        if (booking.getItem().getOwner().getId().equals(booking.getBooker().getId())) {
            throw new NotItemOwnerException(String.format("user with id = %d cannot book their item.",
                    booking.getBooker().getId()));
        }
        booking.setStatus(BookingStatus.WAITING);
        Booking savedBooking = bookingRepository.save(booking);
        log.info("booking with id = {} is saved {}.", savedBooking.getId(), savedBooking);
        return bookingMapper.toBookingDto(savedBooking);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public BookingResponseDto changeStatus(Long userId, Long bookingId, Boolean isApproved) {
        log.info("request to status change a booking with id = {}.", bookingId);
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
