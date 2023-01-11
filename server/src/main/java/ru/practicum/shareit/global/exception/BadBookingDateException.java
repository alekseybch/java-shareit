package ru.practicum.shareit.global.exception;

public class BadBookingDateException extends RuntimeException {
    public BadBookingDateException(String massage) {
        super(massage);
    }
}
