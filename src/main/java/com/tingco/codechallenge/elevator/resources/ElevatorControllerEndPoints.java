package com.tingco.codechallenge.elevator.resources;

import com.tingco.codechallenge.elevator.api.Elevator;
import com.tingco.codechallenge.elevator.api.ElevatorControllerImpl;
import com.tingco.codechallenge.elevator.exception.NotFoundException;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Rest Resource.
 *
 * @author Sven Wesley
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

    @GetMapping(value = "/elevators", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Elevator> getElevatorsSnapshot() {
        return controller.getElevators();
    }

    @PutMapping(value = "/elevators/{id}/release")
    public ResponseEntity<Void> releaseElevator(@PathVariable("id") Integer elevatorId) {
        Elevator elevator = controller.getElevators()
            .stream()
            .filter(e -> e.getId() == elevatorId)
            .findFirst().orElseThrow(() -> new NotFoundException("Elevator %s not found", elevatorId));
        controller.releaseElevator(elevator);
        return ResponseEntity.noContent().build();
    }

}
