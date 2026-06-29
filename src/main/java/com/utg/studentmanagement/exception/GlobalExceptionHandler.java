package com.utg.studentmanagement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

// Watches ALL controllers and intercepts exceptions before they reach the user
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Triggered when: studentService.getStudentById(99) — ID 99 doesn't exist
    // Returns: 404 Not Found + a message
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(ResourceNotFoundException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    // Triggered when: trying to create a student with an email that already exists
    // Returns: 400 Bad Request + a message
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleBadRequest(IllegalArgumentException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // Triggered when: @Valid catches a broken field — e.g. missing @NotBlank, invalid @Email
    // Returns: 400 Bad Request + a list of ALL validation errors at once
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        // Loop through every field that failed validation
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        // Example response:
        // { "email": "Please provide a valid email address", "firstName": "First name is required" }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
}