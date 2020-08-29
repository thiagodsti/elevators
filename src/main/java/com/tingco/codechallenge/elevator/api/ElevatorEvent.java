package com.tingco.codechallenge.elevator.api;

public class ElevatorEvent {

    private final Elevator elevator;
    private final int toFloor;

    public ElevatorEvent(Elevator elevator, int toFloor) {
        this.elevator = elevator;
        this.toFloor = toFloor;
    }

    public Elevator getElevator() {
        return elevator;
    }

    public int getToFloor() {
        return toFloor;
    }
}
