package com.petbuddyz.petbuddyzapp.Notification;

/**
 * Exception thrown when a notification is not found.
 */
class NotificationNotFoundException extends RuntimeException {

  /**
   * Constructs a new NotificationNotFoundException with the specified detail message.
   *
   * @param message the detail message
   */
  NotificationNotFoundException(String message) {
    super(message);
  }
}