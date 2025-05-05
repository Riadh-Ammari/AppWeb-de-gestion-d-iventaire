package com.example.FactureService.service.exception;

public class FactureNotFoundException extends RuntimeException {
    public FactureNotFoundException(String message) {
        super(message);
    }
}
