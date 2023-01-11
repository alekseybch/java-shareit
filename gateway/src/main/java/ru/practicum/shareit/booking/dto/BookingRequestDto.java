package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequestDto {
    @NotNull
    private Long itemId;
    @NotNull
    @FutureOrPresent(message = "date must be in the present or future.")
    private LocalDateTime start;
    @NotNull
    @Future(message = "date must be in the future.")
    private LocalDateTime end;
}
