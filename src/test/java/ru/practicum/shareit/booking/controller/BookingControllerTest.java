package ru.practicum.shareit.booking.controller;

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
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.global.exception.BadStateException;
import ru.practicum.shareit.global.exception.NotItemOwnerException;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;

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
class BookingControllerTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    @Test
    @SneakyThrows
    void findBookingById_whenUserIsBookerOrItemOwner_thenReturnedBooking() {
        this.mockMvc.perform(get("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 3))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.start").isNotEmpty())
                .andExpect(jsonPath("$.end").isNotEmpty())
                .andExpect(jsonPath("$.item").isNotEmpty())
                .andExpect(jsonPath("$.booker").isNotEmpty())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    @SneakyThrows
    void findBookingById_whenUserIsNotBookerOrItemOwner_thenNotItemOwnerException() {
        this.mockMvc.perform(
                        get("/bookings/{bookingId}", 1L)
                                .header("X-Sharer-User-Id", -1))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof NotItemOwnerException));
    }

    @Test
    @SneakyThrows
    void findBookingByState_whenUserFoundAndDefaultState_thenReturnedAllBookings() {
        this.mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 2)
                        .param("from", String.valueOf(1))
                        .param("size", String.valueOf(10)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(3))
                .andExpect(jsonPath("$[0].start").isNotEmpty())
                .andExpect(jsonPath("$[0].end").isNotEmpty())
                .andExpect(jsonPath("$[0].item").isNotEmpty())
                .andExpect(jsonPath("$[0].booker").isNotEmpty())
                .andExpect(jsonPath("$[0].status").value("WAITING"));
    }

    @Test
    @SneakyThrows
    void findBookingByState_whenUserNotFound_thenEntityNotFoundException() {
        this.mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", -1)
                        .param("from", String.valueOf(1))
                        .param("size", String.valueOf(10)))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof EntityNotFoundException));
    }

    @Test
    @SneakyThrows
    void findBookingByState_whenStateUnknows_thenBadStateException() {
        this.mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 2)
                        .param("from", String.valueOf(1))
                        .param("size", String.valueOf(10))
                        .param("state", "UNKNOWN"))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BadStateException));
    }

    @Test
    @SneakyThrows
    void findBookingByStateForOwner_whenUserFoundAndDefaultState_thenReturnedAllOwnerBookings() {
        this.mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", String.valueOf(1))
                        .param("size", String.valueOf(10)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].start").isNotEmpty())
                .andExpect(jsonPath("$[0].end").isNotEmpty())
                .andExpect(jsonPath("$[0].item").isNotEmpty())
                .andExpect(jsonPath("$[0].booker").isNotEmpty())
                .andExpect(jsonPath("$[0].status").value("APPROVED"));
    }

    @Test
    @SneakyThrows
    void findBookingByStateForOwner_whenUserNotFound_thenEntityNotFoundException() {
        this.mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", -1)
                        .param("from", String.valueOf(1))
                        .param("size", String.valueOf(10)))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof EntityNotFoundException));
    }

    @Test
    @SneakyThrows
    void findBookingByStateForOwner_whenStateUnknows_thenBadStateException() {
        this.mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 2)
                        .param("from", String.valueOf(1))
                        .param("size", String.valueOf(10))
                        .param("state", "UNKNOWN"))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BadStateException));
    }

    @Test
    @SneakyThrows
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void saveBooking_whenRequestToSaveBooking_thenSavedAndReturnedBooking() {
        BookingRequestDto bookingRequestDto = new BookingRequestDto();
        bookingRequestDto.setItemId(3L);
        bookingRequestDto.setStart(LocalDateTime.now().plusDays(1));
        bookingRequestDto.setEnd(LocalDateTime.now().plusDays(3));

        String body = objectMapper.writeValueAsString(bookingRequestDto);

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(6))
                .andExpect(jsonPath("$.start").isNotEmpty())
                .andExpect(jsonPath("$.end").isNotEmpty())
                .andExpect(jsonPath("$.item").isNotEmpty())
                .andExpect(jsonPath("$.booker").isNotEmpty())
                .andExpect(jsonPath("$.status").value("WAITING"));
    }

    @Test
    @SneakyThrows
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void saveBooking_whenBadValidate_thenMethodArgumentNotValidException() {
        BookingRequestDto bookingRequestDto = new BookingRequestDto();

        String body = objectMapper.writeValueAsString(bookingRequestDto);

        mockMvc.perform(post("/bookings")
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
    void changeBookingStatus_whenApprovedTrue_thenReturnedChangedBooking() {
        mockMvc.perform(patch("/bookings/{bookingId}", 3L)
                        .header("X-Sharer-User-Id", 3)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.start").isNotEmpty())
                .andExpect(jsonPath("$.end").isNotEmpty())
                .andExpect(jsonPath("$.item").isNotEmpty())
                .andExpect(jsonPath("$.booker").isNotEmpty())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    @SneakyThrows
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void changeBookingStatus_whenBadValidate_thenMethodArgumentTypeMismatchException() {
        mockMvc.perform(patch("/bookings/{bookingId}", 3L)
                        .header("X-Sharer-User-Id", 3)
                        .param("approved", "UNKNOWN"))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof MethodArgumentTypeMismatchException));
    }
}