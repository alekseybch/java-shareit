package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.practicum.shareit.request.dto.ItemReqRequestDto;
import ru.practicum.shareit.request.dto.ItemReqResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemRequestService itemRequestService;

    private final ItemReqResponseDto itemReqResponseDto = new ItemReqResponseDto();

    @Test
    @SneakyThrows
    void findItemRequests_whenInvoked_thenReturnedItemRequests() {
        when(itemRequestService.getItemRequests(anyLong(), anyInt(), anyInt())).thenReturn(List.of(itemReqResponseDto));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", String.valueOf(1))
                        .param("size", String.valueOf(10)))
                .andExpect(status().isOk());

        verify(itemRequestService, times(1)).getItemRequests(anyLong(), anyInt(), anyInt());
    }

    @Test
    @SneakyThrows
    void findItemRequestById_whenRequestIdIsFound_thenReturnedItemRequest() {
        when(itemRequestService.getById(anyLong(), anyLong())).thenReturn(itemReqResponseDto);

        mockMvc.perform(get("/requests/{requestId}", 1)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());

        verify(itemRequestService, times(1)).getById(anyLong(), anyLong());
    }

    @Test
    @SneakyThrows
    void findItemRequestsByOtherRequestors_whenInvoked_thenReturnedItemRequests() {
        when(itemRequestService.getAllByOtherRequestors(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(itemReqResponseDto));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", String.valueOf(1))
                        .param("size", String.valueOf(10)))
                .andExpect(status().isOk());

        verify(itemRequestService, times(1))
                .getAllByOtherRequestors(anyLong(), anyInt(), anyInt());
    }

    @Test
    @SneakyThrows
    void saveItemRequest_whenRequestToSaveItemRequest_thenSavedAndReturnedItemRequest() {
        ItemReqRequestDto itemReqRequestDto = new ItemReqRequestDto();
        itemReqRequestDto.setDescription("need beer");

        itemReqResponseDto.setDescription("need beer");

        when(itemRequestService.save(any())).thenReturn(itemReqResponseDto);

        String body = objectMapper.writeValueAsString(itemReqRequestDto);

        String result = mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemReqResponseDto), result);
    }

    @Test
    @SneakyThrows
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void saveItemRequest_whenBadValidate_thenMethodArgumentNotValidException() {
        ItemReqRequestDto itemReqRequestDto = new ItemReqRequestDto();

        String body = objectMapper.writeValueAsString(itemReqRequestDto);

        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof MethodArgumentNotValidException));

        verify(itemRequestService, times(0)).save(any());
    }
}