package com.tingco.codechallenge.elevator.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.Comparator;
import java.util.Objects;

public class ElevatorImpl implements Elevator, Comparable<Elevator> {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final int id;
    private int addressedFloor;
    private boolean busy;
    private int currentFloor;
    private Direction direction;


    public ElevatorImpl(int id) {
        this.id = id;
        addressedFloor = 0;
        currentFloor = 0;
        busy = false;
        this.direction = Direction.NONE;
    }


    @Override
    public Direction getDirection() {
        return direction;
    }

    @Override
    public int getAddressedFloor() {
        return addressedFloor;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void moveElevator(int toFloor) {
        synchronized (this) {
            busy = true;
            this.addressedFloor = toFloor;
            if (currentFloor < addressedFloor) {
                direction = Direction.UP;
            } else if (currentFloor > addressedFloor) {
                direction = Direction.DOWN;
            } else {
                direction = Direction.NONE;
                return;
            }


            if (Direction.DOWN.equals(direction)) {
                moveDown();
                busy = false;
                return;
            }
            moveTop();
            busy = false;
        }
    }

    private void moveTop() {
        while (currentFloor != addressedFloor) {
            currentFloor++;
            timeTakenToNextFloor();
        }
        logFinalFloor();

    }

    private void logFinalFloor() {
        LOG.info("Elevator id {} arrived to final floor {}", id, currentFloor);
    }

    private void moveDown() {
        while (currentFloor != addressedFloor) {
            currentFloor--;
            timeTakenToNextFloor();
        }
        logFinalFloor();
    }

    private void timeTakenToNextFloor() {
        try {
            LOG.info("Elevator id {} moving to floor {}. Final floor {}", id, currentFloor, addressedFloor);
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            LOG.error("Elevator stopped");
            direction = Direction.NONE;
        }
    }

    @Override
    public boolean isBusy() {
        return busy;
    }

    @Override
    public int currentFloor() {
        return currentFloor;
    }

    @Override
    public int compareTo(Elevator o) {
        return Integer.compare(currentFloor, o.currentFloor());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ElevatorImpl elevator = (ElevatorImpl) o;
        return id == elevator.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
