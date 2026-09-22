package ru.demo.tickets.dto;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public record SegmentResponse(
        Long id,
        String operationCode,
        OffsetDateTime operationTime,
        String operationPlace,
        String passengerName,
        String passengerSurname,
        String passengerPatronymic,
        String documentType,
        String documentNumber,
        LocalDate birthdate,
        String gender,
        String passengerType,
        String ticketNumber,
        int ticketType,
        int serialNumber,
        String airlineCode,
        int flightNumber,
        String departPlace,
        OffsetDateTime departDatetime,
        String arrivePlace,
        OffsetDateTime arriveDatetime,
        String pnrId,
        boolean refunded,
        OffsetDateTime refundTime
) {
}

