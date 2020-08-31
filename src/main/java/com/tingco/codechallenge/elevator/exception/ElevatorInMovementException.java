package com.tingco.codechallenge.elevator.exception;

public class ElevatorInMovementException extends RuntimeException {

    public ElevatorInMovementException() {
        this("Elevator should not be released in movement");
    }

    public ElevatorInMovementException(String message) {
        super(message);
    }
}
