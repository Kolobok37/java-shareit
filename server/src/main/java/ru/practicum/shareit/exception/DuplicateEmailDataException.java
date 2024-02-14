package ru.practicum.shareit.exception;

public class DuplicateEmailDataException extends ValidationDataException {
    public DuplicateEmailDataException(String message) {
        super(message);
    }
}
