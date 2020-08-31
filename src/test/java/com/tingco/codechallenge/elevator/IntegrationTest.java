package com.tingco.codechallenge.elevator;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import com.jayway.awaitility.Awaitility;
import com.jayway.awaitility.Duration;
import com.tingco.codechallenge.elevator.api.Elevator;
import com.tingco.codechallenge.elevator.api.ElevatorControllerImpl;
import com.tingco.codechallenge.elevator.api.ElevatorImpl;
import com.tingco.codechallenge.elevator.api.UserInputProvider;
import com.tingco.codechallenge.elevator.config.ElevatorApplication;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Boiler plate test class to get up and running with a test faster.
 *
 * @author Sven Wesley
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ElevatorApplication.class)
public class IntegrationTest {

    @Autowired
    private ElevatorControllerImpl controller;

    @MockBean
    private UserInputProvider dummyInputProvider;

    @Test
    @DirtiesContext
    public void simulateAnElevatorShaft() {
        when(dummyInputProvider.receiveInputFromUser(anyInt())).thenReturn(Optional.empty());

        ElevatorImpl elevator = (ElevatorImpl) controller.requestElevator(5);
        ElevatorImpl elevator1 = (ElevatorImpl) controller.requestElevator(6);
        ElevatorImpl elevator2 = (ElevatorImpl) controller.requestElevator(4);

        Awaitility.await().until(() -> elevator.isBusy() && elevator1.isBusy() && elevator2.isBusy());
        List<Elevator> elevators = controller.getElevators();
        List<Elevator> collect = elevators.stream().filter(e -> e.isBusy()).collect(Collectors.toList());
        assertThat(collect, hasItems(elevator, elevator1, elevator2));
        collect = elevators.stream().filter(e -> !e.isBusy()).collect(Collectors.toList());
        assertThat(collect, not(hasItems(elevator, elevator1, elevator2)));

        Awaitility.await().timeout(Duration.ONE_MINUTE).until(() -> !elevator.isBusy());
        Awaitility.await().timeout(Duration.ONE_MINUTE).until(() -> !elevator1.isBusy());
        Awaitility.await().timeout(Duration.ONE_MINUTE).until(() -> !elevator2.isBusy());
        assertThat(elevator.currentFloor(), equalTo(5));
        assertThat(elevator1.currentFloor(), equalTo(6));
        assertThat(elevator2.currentFloor(), equalTo(4));
        assertThat(elevator, not(equalTo(elevator1)));
        assertThat(elevator, not(equalTo(elevator2)));
        assertThat(elevator, not(equalTo(elevator2)));

        ElevatorImpl elevator3 = (ElevatorImpl) controller.requestElevator(8);
        assertThat(elevator3, equalTo(elevator1));

        ElevatorImpl elevator4 = (ElevatorImpl) controller.requestElevator(3);
        assertThat(elevator4, equalTo(elevator2));

        //Check if get the nearest elevator to go down
        ElevatorImpl elevator5 = (ElevatorImpl) controller.requestElevator(0);
        assertThat(elevator5, not(equalTo(elevator)));
        assertThat(elevator5, not(equalTo(elevator1)));
        assertThat(elevator5, not(equalTo(elevator2)));
        assertThat(elevator5, not(equalTo(elevator3)));

        //Check if get the nearest elevator to go down
        ElevatorImpl elevator6 = (ElevatorImpl) controller.requestElevator(1);
        assertThat(elevator6, not(equalTo(elevator)));
        assertThat(elevator6, not(equalTo(elevator1)));
        assertThat(elevator6, not(equalTo(elevator2)));
        assertThat(elevator6, not(equalTo(elevator3)));
    }

    @Test
    @DirtiesContext
    public void testWithInputUser() {
        when(dummyInputProvider.receiveInputFromUser(eq(0))).thenReturn(Optional.of(3));
        when(dummyInputProvider.receiveInputFromUser(eq(1))).thenReturn(Optional.empty());

        Elevator elevator = controller.requestElevator(5);
        Elevator elevator1 = controller.requestElevator(3);


        when(dummyInputProvider.receiveInputFromUser(eq(2))).thenReturn(Optional.empty());
        Elevator elevator2 = controller.requestElevator(10);
        Awaitility.await().timeout(Duration.ONE_MINUTE).until(() -> !elevator2.isBusy());
        Elevator elevator3 = controller.requestElevator(8);
        assertThat(elevator2, equalTo(elevator3));

        assertThat(elevator.currentFloor(), equalTo(3));
        assertThat(elevator1.currentFloor(), equalTo(3));
        assertThat(elevator, not(equalTo(elevator1)));


    }

    @Test
    @DirtiesContext
    public void useAllElevators() {
        when(dummyInputProvider.receiveInputFromUser(anyInt())).thenReturn(Optional.empty());
        for (int i = 0; i < controller.getElevators().size() + 3; i++) {
            controller.requestElevator(3 + i);
        }

        List<Elevator> elevators = controller.getElevators().stream().filter(e -> !e.isBusy()).collect(Collectors.toList());
        assertThat(elevators.isEmpty(), equalTo(true));


        Awaitility.await().timeout(Duration.ONE_MINUTE).until(() -> controller.getElevators().stream().filter(e -> !e.isBusy()).collect(Collectors.toList()).size() == controller.getElevators().size());
    }

}
