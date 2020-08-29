package com.tingco.codechallenge.elevator.api;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.AsyncEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class ElevatorControllerImpl implements ElevatorController {

    private final List<Elevator> elevators;
    private final List<Elevator> busyElevators;
    private final Executor executor;

   // private final List<Elevator> elevators;

    public ElevatorControllerImpl(@Value("${com.tingco.elevator.numberofelevators}") int numberOfElevators, Executor executor) {
        this.elevators = IntStream.range(0, numberOfElevators).mapToObj(i -> new ElevatorImpl(i)).collect(Collectors.toList());
        this.executor = executor;
        this.busyElevators = new ArrayList<>();

    }

    @Override
    public Elevator requestElevator(int toFloor) {
        Elevator elevator = elevators.stream().min(Comparator.comparingInt(c -> Math.abs(c.currentFloor() - toFloor))).get();
        elevators.remove(elevator);
        executor.execute(() -> {
            elevator.moveElevator(toFloor);
            releaseElevator(elevator);
        });
        return elevator;
    }

    @Override
    public List<Elevator> getElevators() {
        return elevators;
    }

    @Override
    public void releaseElevator(Elevator elevator) {
        elevators.add(elevator);
    }
}
