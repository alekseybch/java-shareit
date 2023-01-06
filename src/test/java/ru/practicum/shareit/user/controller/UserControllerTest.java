package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.practicum.shareit.user.dto.UserPatchDto;
import ru.practicum.shareit.user.dto.UserRequestDto;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@Sql(scripts = {"file:src/test/resources/schema.sql", "file:src/test/resources/data.sql"})
class UserControllerTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    @Test
    @SneakyThrows
    void findUsers_whenInvoked_thenReturnedAllUsers() {
        this.mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("user"))
                .andExpect(jsonPath("$[0].email").value("user@user.com"));
    }

    @Test
    @SneakyThrows
    void findUserById() {
        this.mockMvc.perform(get("/users/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("user"))
                .andExpect(jsonPath("$.email").value("user@user.com"));
    }

    @Test
    @SneakyThrows
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void saveUser_whenRequestToSaveUser_thenSavedAndReturnedUser() {
        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setName("new");
        userRequestDto.setEmail("new@new.com");

        String body = objectMapper.writeValueAsString(userRequestDto);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.name").value("new"))
                .andExpect(jsonPath("$.email").value("new@new.com"));
    }

    @Test
    @SneakyThrows
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void saveUser_whenBadValidate_thenMethodArgumentNotValidException() {
        UserRequestDto userRequestDto = new UserRequestDto();

        String body = objectMapper.writeValueAsString(userRequestDto);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof MethodArgumentNotValidException));
    }

    @Test
    @SneakyThrows
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void changeUser_whenRequestToChangeUser_thenChangedAndReturnedUser() {
        UserPatchDto userPatchDto = new UserPatchDto();
        userPatchDto.setName("changed");
        userPatchDto.setEmail("changed@changed.com");

        String body = objectMapper.writeValueAsString(userPatchDto);

        mockMvc.perform(patch("/users/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("changed"))
                .andExpect(jsonPath("$.email").value("changed@changed.com"));
    }

    @Test
    @SneakyThrows
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void deleteUser_whenRequestToDeleteUser_thenDeletedUser() {
        this.mockMvc.perform(delete("/users/{id}", 4))
                .andExpect(status().isOk());
    }
}