package ru.practicum.shareit.item.controller;

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
import ru.practicum.shareit.global.exception.BadPageRequestException;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemPatchDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@Sql(scripts = {"file:src/test/resources/schema.sql", "file:src/test/resources/data.sql"})
class ItemControllerTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    @Test
    @SneakyThrows
    void findItems_whenUserIdFound_thenReturnedUserItems() {
        this.mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", String.valueOf(1))
                        .param("size", String.valueOf(10)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("drill"))
                .andExpect(jsonPath("$[0].description").value("simple drill"))
                .andExpect(jsonPath("$[0].available").value("true"))
                .andExpect(jsonPath("$[0].owner").isNotEmpty())
                .andExpect(jsonPath("$[0].lastBooking").isNotEmpty())
                .andExpect(jsonPath("$[0].nextBooking").isNotEmpty())
                .andExpect(jsonPath("$[0].requestId").isEmpty())
                .andExpect(jsonPath("$[0].comments").isNotEmpty());
    }

    @Test
    @SneakyThrows
    void findItems_whenBadSizeValue_thenBadPageRequestException() {
        this.mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", String.valueOf(1))
                        .param("size", String.valueOf(-1)))
                .andExpect(status().is5xxServerError())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BadPageRequestException));
    }

    @Test
    @SneakyThrows
    void findItemById_whenUserIsItemOwner_thenReturnedItem() {
        this.mockMvc.perform(get("/items/{itemId}", 1)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("drill"))
                .andExpect(jsonPath("$.description").value("simple drill"))
                .andExpect(jsonPath("$.available").value("true"))
                .andExpect(jsonPath("$.owner").isNotEmpty())
                .andExpect(jsonPath("$.lastBooking").isNotEmpty())
                .andExpect(jsonPath("$.nextBooking").isNotEmpty())
                .andExpect(jsonPath("$.requestId").isEmpty())
                .andExpect(jsonPath("$.comments").isNotEmpty());
    }

    @Test
    @SneakyThrows
    void findItemByText_whenTextFound_thenReturnedItems() {
        this.mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1)
                        .param("text", "DrILL")
                        .param("from", String.valueOf(1))
                        .param("size", String.valueOf(10)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("drill"))
                .andExpect(jsonPath("$[0].description").value("simple drill"))
                .andExpect(jsonPath("$[0].available").value("true"))
                .andExpect(jsonPath("$[0].owner").isNotEmpty())
                .andExpect(jsonPath("$[0].lastBooking").isNotEmpty())
                .andExpect(jsonPath("$[0].nextBooking").isNotEmpty())
                .andExpect(jsonPath("$[0].requestId").isEmpty())
                .andExpect(jsonPath("$[0].comments").isNotEmpty());
    }

    @Test
    @SneakyThrows
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void saveItem_whenRequestToSaveItem_thenSavedAndReturnedItem() {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setName("hair dryer");
        itemRequestDto.setDescription("powerful hair dryer");
        itemRequestDto.setAvailable(true);

        String body = objectMapper.writeValueAsString(itemRequestDto);

        mockMvc.perform(post("/items")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-Sharer-User-Id", 1)
                    .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(4))
                .andExpect(jsonPath("$.name").value("hair dryer"))
                .andExpect(jsonPath("$.description").value("powerful hair dryer"))
                .andExpect(jsonPath("$.available").value("true"))
                .andExpect(jsonPath("$.owner").isNotEmpty())
                .andExpect(jsonPath("$.lastBooking").isEmpty())
                .andExpect(jsonPath("$.nextBooking").isEmpty())
                .andExpect(jsonPath("$.requestId").isEmpty())
                .andExpect(jsonPath("$.comments").isEmpty());
    }

    @Test
    @SneakyThrows
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
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
    }

    @Test
    @SneakyThrows
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void createComment_whenRequestToSaveComment_thenSavedAndReturnedComment() {
        CommentRequestDto commentRequestDto = new CommentRequestDto();
        commentRequestDto.setText("very nice!");

        String body = objectMapper.writeValueAsString(commentRequestDto);

        mockMvc.perform(post("/items/{itemId}/comment", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 2)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.text").value("very nice!"))
                .andExpect(jsonPath("$.authorName").value("other"))
                .andExpect(jsonPath("$.created").isNotEmpty());
    }

    @Test
    @SneakyThrows
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
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
    }

    @Test
    @SneakyThrows
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void changeItem_whenRequestToChangeItem_thenChangedAndReturnedItem() {
        ItemPatchDto itemPatchDto = new ItemPatchDto();
        itemPatchDto.setName("changed");
        itemPatchDto.setDescription("changed description");
        itemPatchDto.setAvailable(false);

        String body = objectMapper.writeValueAsString(itemPatchDto);

        mockMvc.perform(patch("/items/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("changed"))
                .andExpect(jsonPath("$.description").value("changed description"))
                .andExpect(jsonPath("$.available").value("false"))
                .andExpect(jsonPath("$.owner").isNotEmpty())
                .andExpect(jsonPath("$.lastBooking").isNotEmpty())
                .andExpect(jsonPath("$.nextBooking").isNotEmpty())
                .andExpect(jsonPath("$.requestId").isEmpty())
                .andExpect(jsonPath("$.comments").isNotEmpty());
    }
}