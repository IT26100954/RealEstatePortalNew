package com.realestate.common;

import org.springframework.http.HttpStatus;

// Expected errors carry safe messages and HTTP status codes.
public class ModuleException extends RuntimeException {
    private final HttpStatus status;
    private final String code;
    public ModuleException(HttpStatus status, String code, String message) {
        super(message);
        this.status = status;
        this.code = code;
    }
    public HttpStatus getStatus() { return status; }
    public String getCode() { return code; }
}
