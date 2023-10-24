package ru.practicum.mainService.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpServerErrorException;

import javax.validation.ValidationException;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationError(final ValidationException e) {
        return new ErrorResponse(String.format("validation error: %s", e.getMessage()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleObjectNotFound(final NotFoundException e) {
        return new ErrorResponse(String.format("object not found: %s", e.getMessage()));
    }

    @ExceptionHandler({ConflictException.class,
            HttpMessageNotReadableException.class,
            DataIntegrityViolationException.class,
            HttpServerErrorException.class,
    })
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleValidationException(final RuntimeException e) {
        return new ErrorResponse(String.format("Received status 409 Conflict", e.getMessage()));
    }
}