package ru.demo.tickets.dto;

public record OperationResponse(String operationCode, String ticketNumber, int affectedSegments, String status) {
}

