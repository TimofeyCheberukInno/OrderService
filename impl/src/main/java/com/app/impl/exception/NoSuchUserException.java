package com.app.impl.exception;

public class NoSuchUserException extends RuntimeException {
    public NoSuchUserException(String email) {
        super("No such user with email: " + email);
    }
}
