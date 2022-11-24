package org.food.ordering.order.service.application.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.food.ordering.application.handler.ErrorDTO;
import org.food.ordering.application.handler.GlobalExceptionHandler;
import org.food.ordering.domain.exception.OrderDomainException;
import org.food.ordering.domain.exception.OrderNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
class OrderGlobalExceptionHandler extends GlobalExceptionHandler {

    @ExceptionHandler(OrderDomainException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDTO handleException(OrderDomainException orderDomainException) {
        log.error(orderDomainException.getMessage(), orderDomainException);
        return new ErrorDTO(
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                orderDomainException.getMessage()
        );
    }

    @ExceptionHandler(OrderNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDTO handleException(OrderNotFoundException orderNotFoundException) {
        log.error(orderNotFoundException.getMessage(), orderNotFoundException);
        return new ErrorDTO(
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                orderNotFoundException.getMessage()
        );
    }


}
