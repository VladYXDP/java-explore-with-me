package ru.practicum.ewm.exceptions;

import lombok.Builder;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
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
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFoundException(final NotFoundException e) {
        return ApiError.builder()
                .reason(e.getMessage())
                .status(HttpStatus.NOT_FOUND)
                .timestamp(LocalDateTime.now())
                .errors(Arrays.toString(e.getStackTrace()))
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleBookingDateException(final ForbiddenException e) {
        return ApiError.builder()
                .reason(e.getMessage())
                .status(HttpStatus.BAD_REQUEST)
                .timestamp(LocalDateTime.now())
                .errors(Arrays.toString(e.getStackTrace()))
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleAlreadyExistException(final AlreadyExistException e) {
        return ApiError.builder()
                .reason(e.getMessage())
                .status(HttpStatus.CONFLICT)
                .timestamp(LocalDateTime.now())
                .errors(Arrays.toString(e.getStackTrace()))
                .build();
    }

    @Builder
    public static class ApiError {
        String reason;
        String errors;
        HttpStatus status;
        LocalDateTime timestamp;
    }

}
