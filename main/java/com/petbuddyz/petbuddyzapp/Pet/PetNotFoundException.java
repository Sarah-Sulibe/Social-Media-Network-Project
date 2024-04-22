package com.petbuddyz.petbuddyzapp.Pet;

public class PetNotFoundException extends RuntimeException {
    public PetNotFoundException(String message) {
        super(message);
    }
}