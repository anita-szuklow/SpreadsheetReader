package com.mycompany.spreadsheetreader.exception;

import com.mycompany.spreadsheetreader.exception.SwiftCodeNotFoundException;
import com.mycompany.spreadsheetreader.exception.InvalidSwiftCodeException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidSwiftCodeException.class)
    public ResponseEntity<?> handleInvalidSwift(InvalidSwiftCodeException ex) {
        return ResponseEntity.badRequest().body(Map.of(
            "error", "Invalid SWIFT code format",
            "message", ex.getMessage()
        ));
    }
    
    @ExceptionHandler(InvalidIso2Exception.class)
    public ResponseEntity<?> handleInvalidIso2(InvalidIso2Exception ex) {
        return ResponseEntity.badRequest().body(Map.of(
            "error", "Invalid country ISO2 format",
            "message", ex.getMessage()
        ));
    }

    @ExceptionHandler(SwiftCodeNotFoundException.class)
    public ResponseEntity<?> handleNotFound(SwiftCodeNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
            "error", "SWIFT code not found",
            "message", ex.getMessage()
        ));
    }
    
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleInvalidJson(HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest().body(Map.of(
            "error", "Invalid JSON format",
            "details", ex.getMostSpecificCause().getMessage()
        ));
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<?> handleDbError(DataAccessException ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Map.of(
            "error", "Database error",
            "message", "Could not access the database. Try again later."
        ));
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(Map.of("error", "Validation failed", "fields", errors));
    }
    
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        return ResponseEntity
          .status(HttpStatus.CONFLICT)
          .body(Map.of("error", "SWIFT code already exists, please provide a unique code."));
    }

    @ExceptionHandler(HeadquarterFlagMismatchException.class)
    public ResponseEntity<?> handleHeadquarterMismatch(HeadquarterFlagMismatchException ex) {
        return ResponseEntity.badRequest().body(Map.of(
            "error", "SWIFT code/headquarter mismatch",
            "message", ex.getMessage()
        ));
    }
    
    @ExceptionHandler(CountryNotFoundException.class)
    public ResponseEntity<?> handleCountryNotFound(CountryNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
            "error", "Country not found",
            "message", ex.getMessage()
        ));
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleOtherErrors(Exception ex) {
        return ResponseEntity.status(500).body(Map.of(
            "error", "Unexpected error",
            "message", ex.getMessage()
        ));
    }
}
