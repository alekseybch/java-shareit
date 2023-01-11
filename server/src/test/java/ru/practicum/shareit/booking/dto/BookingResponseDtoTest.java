package ru.practicum.shareit.booking.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.db.model.enums.BookingStatus;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.user.dto.UserResponseDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class BookingResponseDtoTest {
    @Autowired
    private JacksonTester<BookingResponseDto> json;

    @Test
    @SneakyThrows
    void testBookingResponseDtoTest() {
        BookingResponseDto bookingResponseDto = new BookingResponseDto();
        ItemResponseDto itemResponseDto = new ItemResponseDto();
        UserResponseDto userResponseDto = new UserResponseDto();
        bookingResponseDto.setId(1L);
        bookingResponseDto.setItem(itemResponseDto);
        bookingResponseDto.setStart(LocalDateTime.now());
        bookingResponseDto.setEnd(LocalDateTime.now().plusDays(2));
        bookingResponseDto.setBooker(userResponseDto);
        bookingResponseDto.setStatus(BookingStatus.APPROVED);

        JsonContent<BookingResponseDto> result = json.write(bookingResponseDto);

        assertThat(result).extractingJsonPathValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathValue("$.item").isNotNull();
        assertThat(result).extractingJsonPathStringValue("$.start").isNotNull();
        assertThat(result).extractingJsonPathStringValue("$.end").isNotNull();
        assertThat(result).extractingJsonPathValue("$.booker").isNotNull();
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("APPROVED");
    }
}