package ru.practicum.ewm.exceptions;

import jakarta.validation.ValidationException;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.practicum.ewm.categories.controller.CategoryControllerAdmin;
import ru.practicum.ewm.categories.controller.CategoryControllerPublic;
import ru.practicum.ewm.compilations.controller.CompilationControllerAdmin;
import ru.practicum.ewm.compilations.controller.CompilationControllerPublic;
import ru.practicum.ewm.events.controller.EventControllerAdmin;
import ru.practicum.ewm.events.controller.EventControllerPrivate;
import ru.practicum.ewm.events.controller.EventControllerPublic;
import ru.practicum.ewm.requests.controller.RequestController;
import ru.practicum.ewm.users.controller.UserController;

import java.time.LocalDateTime;
import java.util.Arrays;

@ControllerAdvice(assignableTypes = {
        UserController.class,
        RequestController.class,
        EventControllerAdmin.class,
        EventControllerPublic.class,
        EventControllerPrivate.class,
        CategoryControllerAdmin.class,
        CategoryControllerPublic.class,
        CompilationControllerAdmin.class,
        CompilationControllerPublic.class,
})
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiError> handleException(final Exception e) {
        return new ResponseEntity<>(ApiError.builder()
                .reason(e.getMessage())
                .status(HttpStatus.BAD_REQUEST)
                .timestamp(LocalDateTime.now())
                .errors(Arrays.toString(e.getStackTrace()))
                .build(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {ValidationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiError> handleMethodArgumentNotValidException(final ValidationException e) {
        return new ResponseEntity<>(ApiError.builder()
                .reason(e.getMessage())
                .status(HttpStatus.BAD_REQUEST)
                .timestamp(LocalDateTime.now())
                .errors(Arrays.toString(e.getStackTrace()))
                .build(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ApiError> handleNotFoundException(final NotFoundException e) {
        return new ResponseEntity<>(ApiError.builder()
                .reason(e.getMessage())
                .status(HttpStatus.NOT_FOUND)
                .timestamp(LocalDateTime.now())
                .errors(Arrays.toString(e.getStackTrace()))
                .build(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ApiError> handleForbiddenException(final ForbiddenException e) {
        return new ResponseEntity<>(ApiError.builder()
                .reason(e.getMessage())
                .status(HttpStatus.CONFLICT)
                .timestamp(LocalDateTime.now())
                .errors(Arrays.toString(e.getStackTrace()))
                .build(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ApiError> handleAlreadyExistException(final AlreadyExistException e) {
        return new ResponseEntity<>(ApiError.builder()
                .reason(e.getMessage())
                .status(HttpStatus.CONFLICT)
                .timestamp(LocalDateTime.now())
                .errors(Arrays.toString(e.getStackTrace()))
                .build(), HttpStatus.CONFLICT);
    }

    @Builder
    @Getter
    @Setter
    public static class ApiError {
        String reason;
        String errors;
        HttpStatus status;
        LocalDateTime timestamp;
    }

}
