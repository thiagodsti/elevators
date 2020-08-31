package com.tingco.codechallenge.elevator.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.Objects;

public class ElevatorImpl implements Elevator {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final int id;
    private int addressedFloor;
    private boolean busy;
    private int currentFloor;
    private Direction direction;
    private final int timeBetweenFloors;

    public ElevatorImpl(int id, int timeBetweenFloors) {
        this.id = id;
        addressedFloor = 0;
        currentFloor = 0;
        busy = false;
        this.direction = Direction.NONE;
        this.timeBetweenFloors = timeBetweenFloors;
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
        } else {
            moveTop();
        }
        direction = Direction.NONE;
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
            Thread.sleep(timeBetweenFloors);
        } catch (InterruptedException e) {
            LOG.error("Elevator stopped");
            direction = Direction.NONE;
        }
    }

    @Override
    public void release() {
        this.busy = false;
    }

    @Override
    public void occupy() {
        this.busy = true;
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
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ElevatorImpl elevator = (ElevatorImpl) o;
        return id == elevator.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
