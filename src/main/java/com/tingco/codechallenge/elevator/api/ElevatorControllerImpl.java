package com.tingco.codechallenge.elevator.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Component
public class ElevatorControllerImpl implements ElevatorController {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final List<Elevator> elevators;
    private final List<Elevator> busyElevators;
    private final Executor executor;
    private final UserInputProvider userInputProvider;
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition stackEmpty = lock.newCondition();


    public ElevatorControllerImpl(@Value("${com.tingco.elevator.numberofelevators}") int numberOfElevators,
                                  @Value("${com.tingco.elevator.timeBetweenFloors}") int timeBetweenFloors,
                                  Executor executor, UserInputProvider userInputProvider) {
        this.userInputProvider = userInputProvider;
        this.elevators = IntStream.range(0, numberOfElevators).mapToObj(i -> new ElevatorImpl(i, timeBetweenFloors)).collect(Collectors.toList());
        this.executor = executor;
        this.busyElevators = new ArrayList<>();
    }

    @Override
    public Elevator requestElevator(int toFloor) {
        try {
            lock.tryLock();
            while (elevators.isEmpty()) {
                try {
                    stackEmpty.await();
                } catch (InterruptedException e) {
                    LOG.error("Interrupted", e);
                }
            }
            //Get available elevator nearest to the target floor
            Elevator elevator = elevators.stream().min(Comparator.comparingInt(c -> Math.abs(c.currentFloor() - toFloor))).get();
            elevators.remove(elevator);
            busyElevators.add(elevator);
            executor.execute(() -> {
                elevator.moveElevator(toFloor);
                waitForNextAction(elevator);
            });
            return elevator;
        } finally {
            lock.unlock();
        }

    }

    /**
     * This is only a representation of a listener in case who requested the elevator wants to move to another floor.
     *
     * @param elevator requested
     */
    public void waitForNextAction(Elevator elevator) {
        LOG.info("Waiting for input from user to new floor for elevator {}", elevator.getId());
        try {
            Optional<Integer> toNewFloor = userInputProvider.receiveInputFromUser(elevator.getId());
            if (!toNewFloor.isPresent()) {
                LOG.info("No input received for elevator {}", elevator.getId());
            } else {
                Integer toFloor = toNewFloor.get();
                LOG.info("Input received moving elevator {} to new floor {}", elevator.getId(), toFloor);
                elevator.moveElevator(toFloor);
            }
        } finally {
            releaseElevator(elevator);
        }
    }

    @Override
    public List<Elevator> getElevators() {
        return Stream.of(elevators, busyElevators).flatMap(Collection::stream).collect(Collectors.toList());
    }

    @Override
    public void releaseElevator(Elevator elevator) {
        try {
            lock.tryLock();
            elevators.add(elevator);
            busyElevators.remove(elevator);
            stackEmpty.signalAll();
        } finally {
            lock.unlock();
        }
    }
}
