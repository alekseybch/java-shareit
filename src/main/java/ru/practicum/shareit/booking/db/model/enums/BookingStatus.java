package ru.practicum.shareit.booking.db.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum BookingStatus {
    WAITING("WAITING"),
    APPROVED("true"),
    REJECTED("false"),
    CANCELED("CANCELED");

    private final String label;
}
