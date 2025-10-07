package com.app.impl.exception;

public class NoSuchItemException extends RuntimeException {
    public NoSuchItemException(Iterable<Long> ids) {
        super("No such items with id: " + ids);
    }
}
