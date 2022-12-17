package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.dto.BookingBookerDto;
import ru.practicum.shareit.global.mapper.EntityMapper;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.db.model.Booking;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.item.db.repository.ItemRepository;
import ru.practicum.shareit.user.db.repository.UserRepository;

@Mapper(componentModel = "spring", uses = {ItemRepository.class, UserRepository.class})
public interface BookingMapper {
    @Mapping(target = "item", source = "dto.itemId", qualifiedBy = EntityMapper.class)
    @Mapping(target = "booker", source = "dto.bookerId", qualifiedBy = EntityMapper.class)
    Booking toBooking(BookingRequestDto dto);

    BookingResponseDto toBookingDto(Booking booking);

    @Mapping(target = "bookerId", source = "booker.id")
    @Mapping(target = "itemId", source = "item.id")
    BookingBookerDto toBookingBookerDto(Booking booking);
}
