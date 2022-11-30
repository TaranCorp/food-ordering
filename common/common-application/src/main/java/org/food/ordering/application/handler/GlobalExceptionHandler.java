package org.food.ordering.application.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.util.stream.Collectors;

import static org.food.ordering.application.handler.ErrorDTO.createBadRequestDTO;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String UNEXPECTED_EXCEPTION_MESSAGE = "Unexpected Exception";
    private static final String VALIDATION_EXCEPTION_MESSAGE = "Provided data are incorrect";

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorDTO handleException(Exception exception) {
        log.error(exception.getMessage(), exception);
        return new ErrorDTO(
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                UNEXPECTED_EXCEPTION_MESSAGE
        );
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDTO handleException(ValidationException exception) {
        log.error(exception.getMessage(), exception);

        return exception instanceof ConstraintViolationException
                ? createBadRequestDTO(extractValidationMessages((ConstraintViolationException) exception))
                : createBadRequestDTO(VALIDATION_EXCEPTION_MESSAGE);
    }

    private String extractValidationMessages(ConstraintViolationException exception) {
        return exception.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(" ,"));
    }
}
