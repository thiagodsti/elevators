package com.tingco.codechallenge.elevator.api;

import java.lang.invoke.MethodHandles;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DummyInputProvider implements UserInputProvider {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final int timeWaitForNewEvent;

    public DummyInputProvider(@Value("${com.tingco.elevator.timeWaitForNewEvent}") int timeWaitForNewEvent) {
        this.timeWaitForNewEvent = timeWaitForNewEvent;
    }

    @Override
    public Optional<Integer> receiveInputFromUser(int id) {
        try {
            Thread.sleep(timeWaitForNewEvent);
        } catch (InterruptedException e) {
            LOG.error("Something happened waiting for input for elevator {}", id, e);
        }
        return Optional.empty();
    }

}
