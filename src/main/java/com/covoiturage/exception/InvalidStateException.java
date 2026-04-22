package com.covoiturage.exception;

public class InvalidStateException extends BusinessException {
    public InvalidStateException(String message) {
        super(message);
    }
}
