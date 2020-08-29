package com.tingco.codechallenge.elevator.resources;

import com.tingco.codechallenge.elevator.api.Elevator;
import com.tingco.codechallenge.elevator.api.ElevatorControllerImpl;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Rest Resource.
 *
 * @author Sven Wesley
 *
 */
@RestController
@RequestMapping("/rest/v1")
public final class ElevatorControllerEndPoints {

    private final ElevatorControllerImpl controller;

    public ElevatorControllerEndPoints(ElevatorControllerImpl controller) {
        this.controller = controller;
    }

    /**
     * Ping service to test if we are alive.
     *
     * @return String pong
     */
    @RequestMapping(value = "/ping", method = RequestMethod.GET)
    public String ping() {

        return "pong";
    }

    @PostMapping(value = "/elevators", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Elevator requestElevator(@RequestBody int toFloor) {
        return controller.requestElevator(toFloor);
    }

    @GetMapping(value = "/elevators", consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<Elevator> getElevatorsSnapshot() {
        return controller.getElevators();
    }

}
