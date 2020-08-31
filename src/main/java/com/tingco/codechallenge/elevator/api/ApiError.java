package com.tingco.codechallenge.elevator.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiError {

    private final String error;
    private final int status;
    private final String path;
    private final String message;

    @JsonCreator
    public ApiError(@JsonProperty("status") int status,
                    @JsonProperty("error") String error,
                    @JsonProperty("message") String message,
                    @JsonProperty("path") String path) {
        this.error = error;
        this.status = status;
        this.path = path;
        this.message = message;
    }

    public ZonedDateTime getTimestamp() {
        return ZonedDateTime.now(ZoneId.of(ZoneOffset.UTC.getId()));
    }

    public int getStatus() {
        return this.status;
    }

    public String getError() {
        return this.error;
    }

    public String getPath() {
        return this.path;
    }

    public String getMessage() {
        return this.message;
    }


}