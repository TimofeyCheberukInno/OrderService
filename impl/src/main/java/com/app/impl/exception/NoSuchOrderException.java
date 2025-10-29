package com.app.impl.exception;

public class NoSuchOrderException extends RuntimeException
{
    public NoSuchOrderException(Iterable<Long> ids) {
        super("No such orders with id: " + ids);
    }
}
