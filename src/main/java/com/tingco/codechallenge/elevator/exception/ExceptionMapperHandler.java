package com.tingco.codechallenge.elevator.exception;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import com.tingco.codechallenge.elevator.api.ApiError;
import java.lang.invoke.MethodHandles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ExceptionMapperHandler extends ResponseEntityExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @ExceptionHandler(NotFoundException.class)
    protected ResponseEntity<Object> handleEntityNotFoundException(NotFoundException ex, WebRequest request) {
        ApiError apiError = new ApiError(NOT_FOUND.value(), NOT_FOUND.getReasonPhrase(), ex.getMessage(), getRequestPath(request));
        LOG.debug(ex.getMessage(), ex);
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(ElevatorInMovementException.class)
    protected ResponseEntity<Object> handleForbiddenException(ElevatorInMovementException ex, WebRequest request) {
        ApiError apiError = new ApiError(FORBIDDEN.value(), FORBIDDEN.getReasonPhrase(), ex.getMessage(), getRequestPath(request));
        LOG.error("Forbidden", ex);
        return buildResponseEntity(apiError);
    }

    private ResponseEntity<Object> buildResponseEntity(ApiError apiError) {
        return new ResponseEntity<>(apiError, HttpStatus.valueOf(apiError.getStatus()));
    }

    private String getRequestPath(WebRequest request) {
        return ((ServletWebRequest) request).getRequest().getRequestURI();
    }
}