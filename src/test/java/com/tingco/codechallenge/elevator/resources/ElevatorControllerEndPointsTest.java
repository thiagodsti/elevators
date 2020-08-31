package com.tingco.codechallenge.elevator.resources;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleAbstractTypeResolver;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.tingco.codechallenge.elevator.api.ApiError;
import com.tingco.codechallenge.elevator.api.Elevator;
import com.tingco.codechallenge.elevator.api.ElevatorImpl;
import com.tingco.codechallenge.elevator.config.ElevatorApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Boiler plate test class to get up and running with a test faster.
 *
 * @author Sven Wesley
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ElevatorApplication.class)
@AutoConfigureMockMvc
public class ElevatorControllerEndPointsTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Test
    public void ping() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(get("/rest/v1/ping")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(String.valueOf(5))).andReturn().getResponse();
        assertThat(response.getStatus(), equalTo(200));
        assertThat(response.getContentAsString(), equalTo("pong"));
    }

    @Test
    public void requestElevator() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(post("/rest/v1/elevators")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(String.valueOf(5))).andReturn().getResponse();
        assertThat(response.getStatus(), equalTo(200));
    }

    @Test
    public void requestManyElevators() throws Exception {
        for (int i=0;i<7;i++) {
            MockHttpServletResponse response = mockMvc.perform(post("/rest/v1/elevators")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(String.valueOf(i + 3))).andReturn().getResponse();
            assertThat(response.getStatus(), equalTo(200));
        }
    }

    @Test
    public void releaseWhenInMovement() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(post("/rest/v1/elevators")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(String.valueOf(5))).andReturn().getResponse();
        assertThat(response.getStatus(), equalTo(200));

        SimpleModule module = new SimpleModule("CustomModel", Version.unknownVersion());
        SimpleAbstractTypeResolver resolver = new SimpleAbstractTypeResolver();
        resolver.addMapping(Elevator.class, ElevatorImpl.class);
        module.setAbstractTypes(resolver);
        mapper.registerModule(module);

        Elevator elevator = mapper.readValue(response.getContentAsString(), Elevator.class);
        response = mockMvc.perform(put("/rest/v1/elevators/{elevatorId}/release", elevator.getId()))
            .andReturn().getResponse();
        ApiError apiError = mapper.readValue(response.getContentAsString(), ApiError.class);
        assertThat(apiError.getStatus(), equalTo(403));
        assertThat(apiError.getMessage(), equalTo("Elevator should not be released in movement"));
    }

}
