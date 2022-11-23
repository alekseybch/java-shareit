package ru.practicum.shareit.exception;

public class NotUserInstanceException extends RuntimeException{
    public NotUserInstanceException(String massage) {
        super(massage);
    }
}
