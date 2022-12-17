package ru.practicum.shareit.global.exception;

public class NotItemBookedException extends RuntimeException {
    public NotItemBookedException(String massage) {
        super(massage);
    }
}
