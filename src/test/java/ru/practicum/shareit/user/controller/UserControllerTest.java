package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.practicum.shareit.user.dto.UserPatchDto;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;

    private final UserResponseDto userResponseDto = new UserResponseDto();

    @Test
    @SneakyThrows
    void findUsers_whenInvoked_thenReturnedAllUsers() {
        when(userService.getUsers()).thenReturn(List.of(userResponseDto));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk());

        verify(userService, times(1)).getUsers();
    }

    @Test
    @SneakyThrows
    void findUserById() {
        when(userService.getById(anyLong())).thenReturn(userResponseDto);

        mockMvc.perform(get("/users/{id}", 1))
                .andExpect(status().isOk());

        verify(userService, times(1)).getById(anyLong());
    }

    @Test
    @SneakyThrows
    void saveUser_whenRequestToSaveUser_thenSavedAndReturnedUser() {
        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setName("new");
        userRequestDto.setEmail("new@new.com");

        userResponseDto.setId(1L);

        when(userService.save(any())).thenReturn(userResponseDto);

        String body = objectMapper.writeValueAsString(userRequestDto);

        String result = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(userResponseDto), result);
    }

    @Test
    @SneakyThrows
    void saveUser_whenBadValidate_thenMethodArgumentNotValidException() {
        UserRequestDto userRequestDto = new UserRequestDto();

        String body = objectMapper.writeValueAsString(userRequestDto);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof MethodArgumentNotValidException));

        verify(userService, times(0)).save(any());
    }

    @Test
    @SneakyThrows
    void changeUser_whenRequestToChangeUser_thenChangedAndReturnedUser() {
        UserPatchDto userPatchDto = new UserPatchDto();

        when(userService.change(anyLong(), any())).thenReturn(userResponseDto);

        String body = objectMapper.writeValueAsString(userPatchDto);

        mockMvc.perform(patch("/users/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());

        verify(userService, times(1)).change(anyLong(), any());
    }

    @Test
    @SneakyThrows
    void deleteUser_whenRequestToDeleteUser_thenDeletedUser() {
        doNothing().when(userService).delete(anyLong());

        mockMvc.perform(delete("/users/{id}", 4))
                .andExpect(status().isOk());

        verify(userService, times(1)).delete(anyLong());
    }
}