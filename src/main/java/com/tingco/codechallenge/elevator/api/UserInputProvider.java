package com.tingco.codechallenge.elevator.api;

import java.util.Optional;

/**
 * Interface to represents the input from the user that is inside the elevator.
 * We have a implementation {@link DummyInputProvider} only for example
 */
public interface UserInputProvider {

    Optional<Integer> receiveInputFromUser(int id);
}
