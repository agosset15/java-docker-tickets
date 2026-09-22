package ru.demo.tickets.service;

public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}

