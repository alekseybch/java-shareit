package ru.practicum.shareit.booking.db.model;

import lombok.Data;
import ru.practicum.shareit.booking.db.model.enums.BookingStatus;
import ru.practicum.shareit.item.db.model.Item;
import ru.practicum.shareit.user.db.model.User;

import java.time.LocalDateTime;

@Data
public class Booking {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Item item;
    private User booker;
    private BookingStatus status;
}
