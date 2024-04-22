package com.petbuddyz.petbuddyzapp.Group;



public class UnauthorizedAccessException extends RuntimeException {
    public UnauthorizedAccessException(String message) {
        super(message);
    }
}