package com.utg.studentmanagement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// This tells Spring: when this exception is thrown, send a 404 response
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    // RuntimeException is Java's built-in error class
    // We're EXTENDING it (inheritance!) to make our own custom error
    public ResourceNotFoundException(String message) {
        super(message);  // passes the message up to RuntimeException
    }
}