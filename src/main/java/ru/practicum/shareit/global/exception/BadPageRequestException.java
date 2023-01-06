package ru.practicum.shareit.global.exception;

public class BadPageRequestException extends RuntimeException {
    public BadPageRequestException(String message) {
        super(message);
    }
}
