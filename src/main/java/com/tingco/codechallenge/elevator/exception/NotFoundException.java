package com.tingco.codechallenge.elevator.exception;

public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String formatMessage, Object... variables) {
        super(String.format(formatMessage, variables));
    }
}
