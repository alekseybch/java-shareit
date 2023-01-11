package ru.practicum.shareit.user.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class UserResponseDtoTest {
    @Autowired
    private JacksonTester<UserResponseDto> json;

    @Test
    @SneakyThrows
    void testUserResponseDtoTest() {
        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setId(1L);
        userResponseDto.setName("Bob");
        userResponseDto.setEmail("email@mail.com");

        JsonContent<UserResponseDto> result = json.write(userResponseDto);

        assertThat(result).extractingJsonPathValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathValue("$.name").isEqualTo("Bob");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("email@mail.com");
    }
}