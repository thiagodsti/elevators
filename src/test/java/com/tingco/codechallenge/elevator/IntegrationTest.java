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
import com.tingco.codechallenge.elevator.exception.ElevatorInMovementException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
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

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    @DirtiesContext
    public void simulateAnElevatorShaft() {
        when(dummyInputProvider.receiveInputFromUser(anyInt())).thenReturn(Optional.empty());

        ElevatorImpl elevator = (ElevatorImpl) controller.requestElevator(5);
        ElevatorImpl elevator1 = (ElevatorImpl) controller.requestElevator(6);
        ElevatorImpl elevator2 = (ElevatorImpl) controller.requestElevator(4);

        Awaitility.await().until(() -> elevator.isBusy() && elevator1.isBusy() && elevator2.isBusy());

        //Here is just to check if the snapshot is correctly
        List<Elevator> elevators = controller.getElevators();
        List<Elevator> collect = elevators.stream().filter(Elevator::isBusy).collect(Collectors.toList());
        assertThat(collect, hasItems(elevator, elevator1, elevator2));
        collect = elevators.stream().filter(e -> !e.isBusy()).collect(Collectors.toList());
        assertThat(collect, not(hasItems(elevator, elevator1, elevator2)));

        Awaitility.await().until(() -> !elevator.isBusy());
        Awaitility.await().until(() -> !elevator1.isBusy());
        Awaitility.await().until(() -> !elevator2.isBusy());
        assertThat(elevator.currentFloor(), equalTo(5));
        assertThat(elevator1.currentFloor(), equalTo(6));
        assertThat(elevator2.currentFloor(), equalTo(4));
        assertThat(elevator, not(equalTo(elevator1)));
        assertThat(elevator, not(equalTo(elevator2)));

        //Ask a new elevator and check if it's closest, in this case elevator 1 that was in floor 6 before.
        ElevatorImpl elevator3 = (ElevatorImpl) controller.requestElevator(8);
        assertThat(elevator3, equalTo(elevator1));

        //Ask a new elevator and check if it's closest, in this case elevator 2 that was in floor 4 before.
        ElevatorImpl elevator4 = (ElevatorImpl) controller.requestElevator(3);
        assertThat(elevator4, equalTo(elevator2));

        //Check if get the nearest elevator to go down, in this case must be some elevator that is already there
        ElevatorImpl elevator5 = (ElevatorImpl) controller.requestElevator(0);
        assertThat(elevator5, not(equalTo(elevator)));
        assertThat(elevator5, not(equalTo(elevator1)));
        assertThat(elevator5, not(equalTo(elevator2)));
        assertThat(elevator5, not(equalTo(elevator3)));

        //Check if get the nearest elevator to go down, in this case any elevator that is in floor 0
        ElevatorImpl elevator6 = (ElevatorImpl) controller.requestElevator(1);
        assertThat(elevator6, not(equalTo(elevator)));
        assertThat(elevator6, not(equalTo(elevator1)));
        assertThat(elevator6, not(equalTo(elevator2)));
        assertThat(elevator6, not(equalTo(elevator3)));
    }

    @Test
    @DirtiesContext
    public void testWithInputUser() {
        //Mock elevator id 0 to go to floor 3 when it stops.
        when(dummyInputProvider.receiveInputFromUser(eq(0))).thenReturn(Optional.of(3));
        when(dummyInputProvider.receiveInputFromUser(eq(1))).thenReturn(Optional.empty());

        Elevator elevator = controller.requestElevator(5);
        Elevator elevator1 = controller.requestElevator(3);

        //Make sure the elevator id 0 arrives on floor 5 so we request a new elevator and a new one must to go there
        //Because the elevator id 0 will go to floor 3 since the user requested.
        Awaitility.await().until(() -> elevator.getAddressedFloor() == 5);
        when(dummyInputProvider.receiveInputFromUser(eq(2))).thenReturn(Optional.empty());
        Elevator elevator2 = controller.requestElevator(10);
        Awaitility.await().until(() -> !elevator2.isBusy());
        Elevator elevator3 = controller.requestElevator(8);
        assertThat(elevator2, equalTo(elevator3));

        //Check if the first elevator that was in floor 10 now is in 3
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

        Awaitility.await().timeout(Duration.TEN_SECONDS.multiply(2)).until(() -> controller.getElevators().stream().anyMatch(Elevator::isBusy));
        assertThat(elevators.stream().filter(Elevator::isBusy).count(), equalTo(0L));
    }

    @Test
    public void releaseWhenInMovement() {
        Elevator elevator = controller.requestElevator(5);
        thrown.expect(ElevatorInMovementException.class);
        thrown.expectMessage("Elevator should not be released in movement");
        controller.releaseElevator(elevator);
    }

}
