package ru.practicum.shareit.item.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingBookerDto;
import ru.practicum.shareit.user.dto.UserResponseDto;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class ItemResponseDtoTest {
    @Autowired
    private JacksonTester<ItemResponseDto> json;

    @Test
    @SneakyThrows
    void testItemResponseDtoTest() {
        ItemResponseDto itemResponseDto = new ItemResponseDto();
        UserResponseDto userResponseDto = new UserResponseDto();
        BookingBookerDto bookingBookerDtoLast = new BookingBookerDto();
        BookingBookerDto bookingBookerDtoNext = new BookingBookerDto();
        List<CommentResponseDto> commentResponseDtoList = new ArrayList<>();
        itemResponseDto.setId(1L);
        itemResponseDto.setName("drill");
        itemResponseDto.setDescription("simple drill");
        itemResponseDto.setAvailable(true);
        itemResponseDto.setOwner(userResponseDto);
        itemResponseDto.setLastBooking(bookingBookerDtoLast);
        itemResponseDto.setNextBooking(bookingBookerDtoNext);
        itemResponseDto.setRequestId(2L);
        itemResponseDto.setComments(commentResponseDtoList);

        JsonContent<ItemResponseDto> result = json.write(itemResponseDto);

        assertThat(result).extractingJsonPathValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathValue("$.name").isEqualTo("drill");
        assertThat(result).extractingJsonPathValue("$.description").isEqualTo("simple drill");
        assertThat(result).extractingJsonPathValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathValue("$.owner").isNotNull();
        assertThat(result).extractingJsonPathValue("$.lastBooking").isNotNull();
        assertThat(result).extractingJsonPathValue("$.nextBooking").isNotNull();
        assertThat(result).extractingJsonPathValue("$.requestId").isEqualTo(2);
        assertThat(result).extractingJsonPathValue("$.comments").isNotNull();
    }
}