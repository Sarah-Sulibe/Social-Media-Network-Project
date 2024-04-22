package com.petbuddyz.petbuddyzapp.Post;

public class PostNotFoundException extends RuntimeException {
    public PostNotFoundException(String message) {
        super(message);
    }
}