package com.petbuddyz.petbuddyzapp.Notification;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.petbuddyz.petbuddyzapp.Owner.Owner;


/**
 * The NotificationRepository interface provides methods to interact with the database
 * for managing notifications.
 */
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByOwnerOrderByNotificationCreationDateDesc(Owner owner);
    // query methods...
}