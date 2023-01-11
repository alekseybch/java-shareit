package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.shareit.booking.db.model.enums.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.service.BookingService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private BookingService bookingService;

    private final BookingResponseDto bookingResponseDto = new BookingResponseDto();

    @Test
    @SneakyThrows
    void getBooking_whenUserIsBookerOrItemOwner_thenReturnedBooking() {
        when(bookingService.getBooking(anyLong(), anyLong())).thenReturn(bookingResponseDto);

        this.mockMvc.perform(get("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 3))
                .andExpect(status().isOk());

        verify(bookingService, times(1)).getBooking(anyLong(), anyLong());
    }

    @Test
    @SneakyThrows
    void getBookings_whenUserFoundAndDefaultState_thenReturnedAllBookings() {
        when(bookingService.getBookings(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingResponseDto));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 2)
                        .param("from", String.valueOf(1))
                        .param("size", String.valueOf(10))
                        .param("state", String.valueOf(BookingState.ALL)))
                .andExpect(status().isOk());

        verify(bookingService, times(1))
                .getBookings(anyLong(), any(), anyInt(), anyInt());
    }

    @Test
    @SneakyThrows
    void getItemOwnerBookings_whenUserFoundAndDefaultState_thenReturnedAllOwnerBookings() {
        when(bookingService.getItemOwnerBookings(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingResponseDto));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", String.valueOf(1))
                        .param("size", String.valueOf(10))
                        .param("state", String.valueOf(BookingState.ALL)))
                .andExpect(status().isOk());

        verify(bookingService, times(1))
                .getItemOwnerBookings(anyLong(), any(), anyInt(), anyInt());
    }

    @Test
    @SneakyThrows
    void saveBooking_whenRequestToSaveBooking_thenSavedAndReturnedBooking() {
        BookingRequestDto bookingRequestDto = new BookingRequestDto();
        bookingRequestDto.setItemId(1L);
        bookingRequestDto.setStart(LocalDateTime.now().plusDays(1));
        bookingRequestDto.setEnd(LocalDateTime.now().plusDays(3));

        bookingResponseDto.setId(1L);

        when(bookingService.bookItem(any())).thenReturn(bookingResponseDto);

        String body = objectMapper.writeValueAsString(bookingRequestDto);

        String result = mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(bookingResponseDto), result);
    }

    @Test
    @SneakyThrows
    void changeBookingStatus_whenApprovedTrue_thenReturnedChangedBooking() {
        bookingResponseDto.setStatus(BookingStatus.APPROVED);

        when(bookingService.updateBookingStatus(anyLong(), anyLong(), anyBoolean())).thenReturn(bookingResponseDto);

        mockMvc.perform(patch("/bookings/{bookingId}", 3L)
                        .header("X-Sharer-User-Id", 3)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));

        verify(bookingService, times(1)).updateBookingStatus(anyLong(), anyLong(), anyBoolean());
    }

    @Test
    @SneakyThrows
    void changeBookingStatus_whenBadValidate_thenMethodArgumentTypeMismatchException() {
        mockMvc.perform(patch("/bookings/{bookingId}", 3L)
                        .header("X-Sharer-User-Id", 3)
                        .param("approved", "UNKNOWN"))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof MethodArgumentTypeMismatchException));

        verify(bookingService, times(0)).updateBookingStatus(anyLong(), anyLong(), anyBoolean());
    }
}