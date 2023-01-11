package ru.practicum.shareit.global.exception;

public class NotItemAvailableException extends RuntimeException {
    public NotItemAvailableException(String massage) {
        super(massage);
    }
}
