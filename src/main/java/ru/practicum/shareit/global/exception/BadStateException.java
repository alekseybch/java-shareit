package ru.practicum.shareit.global.exception;

public class BadStateException extends RuntimeException {
    public BadStateException(String massage) {
        super(massage);
    }
}
