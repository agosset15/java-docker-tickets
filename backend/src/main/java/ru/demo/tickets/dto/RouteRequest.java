package ru.demo.tickets.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.time.OffsetDateTime;

public record RouteRequest(
        @JsonProperty("airline_code") @NotBlank @Pattern(regexp = "[A-Z0-9]{2,3}") String airlineCode,
        @JsonProperty("flight_num") @NotNull @Positive Integer flightNum,
        @JsonProperty("depart_place") @NotBlank @Pattern(regexp = "[A-Z]{3}") String departPlace,
        @JsonProperty("depart_datetime") @NotNull OffsetDateTime departDatetime,
        @JsonProperty("arrive_place") @NotBlank @Pattern(regexp = "[A-Z]{3}") String arrivePlace,
        @JsonProperty("arrive_datetime") @NotNull OffsetDateTime arriveDatetime,
        @JsonProperty("pnr_id") @NotBlank String pnrId
) {
}

