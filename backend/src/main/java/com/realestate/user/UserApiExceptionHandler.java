package com.realestate.user;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.realestate.common.ModuleException;

@RestControllerAdvice(basePackages="com.realestate.user")
public class UserApiExceptionHandler {
    @ExceptionHandler(ModuleException.class)
    public ResponseEntity<Map<String,String>> expected(ModuleException e) { return response(e.getStatus(),e.getCode(),e.getMessage()); }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String,String>> invalid(HttpMessageNotReadableException e) { return response(HttpStatus.BAD_REQUEST,"INVALID_REQUEST","Check the JSON fields and allowed role values."); }
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String,String>> conflict(DataIntegrityViolationException e) { return response(HttpStatus.CONFLICT,"DATA_CONFLICT","A duplicate username or referenced account prevents this change."); }
    @ExceptionHandler(ConcurrencyFailureException.class)
    public ResponseEntity<Map<String,String>> concurrent(ConcurrencyFailureException e) { return response(HttpStatus.CONFLICT,"CONCURRENT_CHANGE","Another account change is in progress. Refresh and retry."); }
    private ResponseEntity<Map<String,String>> response(HttpStatus status,String code,String message) {
        Map<String,String> body=new LinkedHashMap<>();
        body.put("code",code);body.put("message",message);
        return new ResponseEntity<>(body,status);
    }
}
