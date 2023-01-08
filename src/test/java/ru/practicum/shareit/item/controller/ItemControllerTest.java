package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemService itemService;

    private final ItemResponseDto itemResponseDto = new ItemResponseDto();

    @Test
    @SneakyThrows
    void findItems_whenUserIdFound_thenReturnedUserItems() {
        when(itemService.getItems(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(itemResponseDto));

        this.mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", String.valueOf(1))
                        .param("size", String.valueOf(10)))
                .andExpect(status().isOk());

        verify(itemService, times(1)).getItems(anyLong(), anyInt(), anyInt());
    }

    @Test
    @SneakyThrows
    void findItemById_whenUserIsItemOwner_thenReturnedItem() {
        when(itemService.getById(anyLong(), anyLong())).thenReturn(itemResponseDto);

        this.mockMvc.perform(get("/items/{itemId}", 1)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());

        verify(itemService, times(1)).getById(anyLong(), anyLong());
    }

    @Test
    @SneakyThrows
    void findItemByText_whenTextFound_thenReturnedItems() {
        when(itemService.getByText(anyString(), anyInt(), anyInt())).thenReturn(List.of(itemResponseDto));

        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1)
                        .param("text", "DrILL")
                        .param("from", String.valueOf(1))
                        .param("size", String.valueOf(10)))
                .andExpect(status().isOk());

        verify(itemService, times(1)).getByText(anyString(), anyInt(), anyInt());
    }

    @Test
    @SneakyThrows
    void saveItem_whenRequestToSaveItem_thenSavedAndReturnedItem() {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setName("hair dryer");
        itemRequestDto.setDescription("powerful hair dryer");
        itemRequestDto.setAvailable(true);

        itemResponseDto.setId(1L);

        when(itemService.save(any())).thenReturn(itemResponseDto);

        String body = objectMapper.writeValueAsString(itemRequestDto);

        String result = mockMvc.perform(post("/items")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-Sharer-User-Id", 1)
                    .content(body))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemResponseDto), result);
    }

    @Test
    @SneakyThrows
    void saveItem_whenBadValidate_thenMethodArgumentNotValidException() {
        ItemRequestDto itemRequestDto = new ItemRequestDto();

        String body = objectMapper.writeValueAsString(itemRequestDto);

        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof MethodArgumentNotValidException));

        verify(itemService, times(0)).save(any());
    }

    @Test
    @SneakyThrows
    void createComment_whenRequestToSaveComment_thenSavedAndReturnedComment() {
        CommentRequestDto commentRequestDto = new CommentRequestDto();
        commentRequestDto.setText("very nice");
        CommentResponseDto commentResponseDto = new CommentResponseDto();
        commentResponseDto.setText("very nice");

        when(itemService.saveComment(any())).thenReturn(commentResponseDto);

        String body = objectMapper.writeValueAsString(commentRequestDto);

        String result = mockMvc.perform(post("/items/{itemId}/comment", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 2)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(commentResponseDto), result);
    }

    @Test
    @SneakyThrows
    void createComment_whenBadValidate_thenMethodArgumentNotValidException() {
        CommentRequestDto commentRequestDto = new CommentRequestDto();

        String body = objectMapper.writeValueAsString(commentRequestDto);

        mockMvc.perform(post("/items/{itemId}/comment", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 2)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof MethodArgumentNotValidException));

        verify(itemService, times(0)).saveComment(any());
    }

    @Test
    @SneakyThrows
    void changeItem_whenRequestToChangeItem_thenChangedAndReturnedItem() {
        ItemPatchDto itemPatchDto = new ItemPatchDto();

        when(itemService.change(anyLong(), anyLong(), any())).thenReturn(itemResponseDto);

        String body = objectMapper.writeValueAsString(itemPatchDto);

        mockMvc.perform(patch("/items/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .content(body))
                .andExpect(status().isOk());

        verify(itemService, times(1)).change(anyLong(), anyLong(), any());
    }
}