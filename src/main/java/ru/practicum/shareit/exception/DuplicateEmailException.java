package ru.practicum.shareit.exception;

public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException(String massage) {
        super(massage);
    }
}
