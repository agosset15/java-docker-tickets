package ru.demo.tickets.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.OffsetDateTime;

public record RefundRequest(
        @JsonProperty("operation_type") @NotBlank @Pattern(regexp = "refund") String operationType,
        @JsonProperty("operation_time") @NotNull OffsetDateTime operationTime,
        @JsonProperty("operation_place") @NotBlank String operationPlace,
        @JsonProperty("ticket_number") @NotBlank @Pattern(regexp = "\\d{13}") String ticketNumber
) {
}

