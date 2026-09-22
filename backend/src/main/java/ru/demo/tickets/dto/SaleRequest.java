package ru.demo.tickets.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.OffsetDateTime;
import java.util.List;

public record SaleRequest(
        @JsonProperty("operation_type") @NotBlank @Pattern(regexp = "sale") String operationType,
        @JsonProperty("operation_time") @NotNull OffsetDateTime operationTime,
        @JsonProperty("operation_place") @NotBlank String operationPlace,
        @Valid @NotNull PassengerRequest passenger,
        @Valid @NotEmpty List<RouteRequest> routes
) {
}

