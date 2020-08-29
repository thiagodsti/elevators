package com.tingco.codechallenge.elevator;

import com.jayway.awaitility.Awaitility;
import com.jayway.awaitility.Duration;
import com.tingco.codechallenge.elevator.api.ElevatorControllerImpl;
import com.tingco.codechallenge.elevator.api.ElevatorImpl;
import com.tingco.codechallenge.elevator.config.ElevatorApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Boiler plate test class to get up and running with a test faster.
 *
 * @author Sven Wesley
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ElevatorApplication.class)
public class IntegrationTest {

    @Autowired
    private ElevatorControllerImpl controller;

    @Test
    public void simulateAnElevatorShaft() {
        ElevatorImpl elevator = (ElevatorImpl) controller.requestElevator(5);

        ElevatorImpl elevator1 = (ElevatorImpl) controller.requestElevator(6);
        ElevatorImpl elevator2 = (ElevatorImpl) controller.requestElevator(2);

        Awaitility.await().timeout(Duration.TWO_MINUTES).until(() -> !elevator.isBusy());
        Awaitility.await().timeout(Duration.TWO_MINUTES).until(() -> !elevator1.isBusy());
        Awaitility.await().timeout(Duration.TWO_MINUTES).until(() -> !elevator2.isBusy());




    }

}
