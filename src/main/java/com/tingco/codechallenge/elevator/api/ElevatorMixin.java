package com.tingco.codechallenge.elevator.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tingco.codechallenge.elevator.api.Elevator.Direction;

public abstract class ElevatorMixin {

    @JsonCreator
    public ElevatorMixin(@JsonProperty("id") int id,
                         @JsonProperty("addressedFloor") int addressedFloor,
                         @JsonProperty("busy") boolean busy,
                         @JsonProperty("currentFloor") int currentFloor,
                         @JsonProperty("direction") Direction direction,
                         @JsonProperty("timeBetweenFloors") int timeBetweenFloors) {
    }
}
