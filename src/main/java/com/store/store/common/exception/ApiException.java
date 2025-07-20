package com.store.store.common.exception;

import java.util.List;

public class ApiException extends RuntimeException {
    private final int statusCode;
    private final List<String> errors;

    public ApiException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
        this.errors = List.of(message);
    }

    public ApiException(int statusCode, String message, List<String> errors) {
        super(message);
        this.statusCode = statusCode;
        this.errors = errors;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public List<String> getErrors() {
        return errors;
    }
}
