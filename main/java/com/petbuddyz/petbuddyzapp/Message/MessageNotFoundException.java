package com.petbuddyz.petbuddyzapp.Message;

/**
 * Exception thrown when a message is not found.
 */
public class MessageNotFoundException extends RuntimeException {
    /**
     * Constructs a new MessageNotFoundException with the specified message ID.
     *
     * @param id the ID of the message that was not found
     */
    public MessageNotFoundException(String message) {
        super(message);
    }
}