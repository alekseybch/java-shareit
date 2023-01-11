package ru.practicum.shareit.global.exception;

public class BadApproveStatusException extends RuntimeException {
    public BadApproveStatusException(String massage) {
        super(massage);
    }
}
