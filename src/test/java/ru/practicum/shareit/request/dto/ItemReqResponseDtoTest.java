package ru.practicum.shareit.request.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemForRequestResponseDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class ItemReqResponseDtoTest {
    @Autowired
    private JacksonTester<ItemReqResponseDto> json;

    @Test
    @SneakyThrows
    void testItemReqResponseDtoTest() {
        ItemReqResponseDto itemReqResponseDto = new ItemReqResponseDto();
        List<ItemForRequestResponseDto> itemForRequestResponseDtoList = new ArrayList<>();
        itemReqResponseDto.setId(1L);
        itemReqResponseDto.setDescription("simple item");
        itemReqResponseDto.setCreated(LocalDateTime.now());
        itemReqResponseDto.setItems(itemForRequestResponseDtoList);

        JsonContent<ItemReqResponseDto> result = json.write(itemReqResponseDto);

        assertThat(result).extractingJsonPathValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathValue("$.description").isEqualTo("simple item");
        assertThat(result).extractingJsonPathValue("$.created").isNotNull();
        assertThat(result).extractingJsonPathValue("$.items").isNotNull();
    }
}