package ru.demo.tickets.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

public record PassengerRequest(
        @NotBlank String name,
        @NotBlank String surname,
        String patronymic,
        @JsonProperty("doc_type") @NotBlank String docType,
        @JsonProperty("doc_number") @NotBlank String docNumber,
        @NotNull LocalDate birthdate,
        @NotBlank @Pattern(regexp = "[MF]") String gender,
        @JsonProperty("passenger_type") @NotBlank String passengerType,
        @JsonProperty("ticket_number") @NotBlank @Pattern(regexp = "\\d{13}") String ticketNumber,
        @JsonProperty("ticket_type") @NotNull Integer ticketType
) {
}

