package org.food.ordering.application.handler;

import org.springframework.http.HttpStatus;

public class ErrorDTO {
    private final String code;
    private final String message;

    public ErrorDTO(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public static ErrorDTO createBadRequestDTO(String message) {
        return new ErrorDTO(
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                message
        );
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
