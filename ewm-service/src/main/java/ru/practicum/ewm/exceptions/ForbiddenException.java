package ru.practicum.ewm.exceptions;

//@ResponseStatus(HttpStatus.CONFLICT)
public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) {
        super(message);
    }
}
