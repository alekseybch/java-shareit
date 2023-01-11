package ru.practicum.shareit.global.exception;

public class NotItemOwnerException extends RuntimeException {
    public NotItemOwnerException(String massage) {
        super(massage);
    }
}
