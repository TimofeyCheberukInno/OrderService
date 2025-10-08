package com.app.impl.advice;

public record ErrorResponse(
        int statusCode,
        String message,
        String url
) { }
