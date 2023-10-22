package ru.practicum.shareit.exception;

public class DuplicateEmailException extends ValidationException{
    public DuplicateEmailException(String message) {
        super(message);
    }
}
