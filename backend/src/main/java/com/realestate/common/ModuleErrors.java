package com.realestate.common;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

// Limited to the five modules: does not replace Member 1's account advice.
@RestControllerAdvice(basePackages = {
    "com.realestate.property", "com.realestate.moving", "com.realestate.appointment",
    "com.realestate.offer", "com.realestate.review"
})
public class ModuleErrors {
    @ExceptionHandler(ModuleException.class)
    public ResponseEntity<Map<String, String>> expected(ModuleException e) {
        return response(e.getStatus(), e.getCode(), e.getMessage());
    }
    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<Map<String, String>> invalid(Exception e) {
        return response(HttpStatus.BAD_REQUEST, "INVALID_REQUEST", "Check JSON fields, dates, numbers and allowed values.");
    }
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> conflict(DataIntegrityViolationException e) {
        return response(HttpStatus.CONFLICT, "DATA_CONFLICT", "A referenced record or database constraint prevents this change.");
    }
    @ExceptionHandler(ConcurrencyFailureException.class)
    public ResponseEntity<Map<String, String>> concurrent(ConcurrencyFailureException e) {
        return response(HttpStatus.CONFLICT, "CONCURRENT_CHANGE", "Another change is in progress. Refresh and retry.");
    }
    private ResponseEntity<Map<String, String>> response(HttpStatus status, String code, String message) {
        Map<String, String> body = new LinkedHashMap<>();
        body.put("code", code);
        body.put("message", message);
        return new ResponseEntity<>(body, status);
    }
}
