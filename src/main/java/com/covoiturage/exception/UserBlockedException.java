package com.covoiturage.exception;

public class UserBlockedException extends BusinessException {
    public UserBlockedException(String message) {
        super(message);
    }
}
