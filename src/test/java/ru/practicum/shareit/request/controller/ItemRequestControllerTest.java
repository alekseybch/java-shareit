package ru.practicum.shareit.request.controller;

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
import ru.practicum.shareit.request.dto.ItemReqRequestDto;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@Sql(scripts = {"file:src/test/resources/schema.sql", "file:src/test/resources/data.sql"})
class ItemRequestControllerTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    @Test
    @SneakyThrows
    void findItemRequests_whenInvoked_thenReturnedItemRequests() {
        this.mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", String.valueOf(1))
                        .param("size", String.valueOf(10)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].description").value("need best juicer"))
                .andExpect(jsonPath("$[0].created").isNotEmpty())
                .andExpect(jsonPath("$[0].items").isNotEmpty());
    }

    @Test
    @SneakyThrows
    void findItemRequestById_whenRequestIdIsFound_thenReturnedItemRequest() {
        this.mockMvc.perform(get("/requests/{requestId}", 1)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("need best juicer"))
                .andExpect(jsonPath("$.created").isNotEmpty())
                .andExpect(jsonPath("$.items").isNotEmpty());
    }

    @Test
    @SneakyThrows
    void findItemRequestsByOtherRequestors_whenInvoked_thenReturnedItemRequests() {
        this.mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", String.valueOf(1))
                        .param("size", String.valueOf(10)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[0].description").value("need something"))
                .andExpect(jsonPath("$[0].created").isNotEmpty())
                .andExpect(jsonPath("$[0].items").isEmpty());
    }

    @Test
    @SneakyThrows
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void saveItemRequest_whenRequestToSaveItemRequest_thenSavedAndReturnedItemRequest() {
        ItemReqRequestDto itemReqRequestDto = new ItemReqRequestDto();
        itemReqRequestDto.setDescription("need beer");

        String body = objectMapper.writeValueAsString(itemReqRequestDto);

        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.description").value("need beer"))
                .andExpect(jsonPath("$.created").isNotEmpty())
                .andExpect(jsonPath("$.items").isEmpty());
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
    }
}