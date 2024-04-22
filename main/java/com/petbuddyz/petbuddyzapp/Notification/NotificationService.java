package com.petbuddyz.petbuddyzapp.Notification;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.petbuddyz.petbuddyzapp.Owner.Owner;
import com.petbuddyz.petbuddyzapp.Owner.OwnerNotFoundException;
import com.petbuddyz.petbuddyzapp.Owner.OwnerRepository;

/**
 * The NotificationService class is responsible for managing notifications and
 * sending them to users.
 * It provides methods to create, send, retrieve, mark as read, and delete
 * notifications.
 */
@Service
public class NotificationService {
    // service methods...

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private NotificationModelAssembler assembler;

    @Autowired
    private OwnerRepository ownerRepository;

    public NotificationService(NotificationRepository repository, NotificationModelAssembler assembler,
            OwnerRepository ownerRepository) {
        this.notificationRepository = repository;
        this.assembler = assembler;
        this.ownerRepository = ownerRepository;

    }

    public Notification createNotification(Long ownerId, String message) {
        // Validate input parameters
        if (ownerId == null || message == null) {
            throw new IllegalArgumentException("Owner ID and message cannot be null");
        }

        // Check if the owner exists
        Optional<Owner> ownerOptional = ownerRepository.findById(ownerId);
        Owner owner = ownerOptional
                .orElseThrow(() -> new OwnerNotFoundException("Owner not found with ID: " + ownerId));

        // Create the notification
        Notification notification = new Notification();
        notification.setOwner(owner);
        notification.setContent(message);


        // Setting status to UNREAD by default
        notification.setStatus(Notification.Status.UNREAD);
        // Save the notification
        return notificationRepository.save(notification);
    }
    // @PostMapping
    // ResponseEntity<?> add(@Valid @RequestBody Notification notification) {
    //     EntityModel<Notification> entityModel = assembler.toModel(repository.save(notification));
    //     return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(entityModel);
    // }
    // Method to send a notification to a user
    public Notification sendNotification(Long ownerId, String message, Notification.NotificationType type) {
        Notification notification = new Notification();
        Owner owner = new Owner();
        owner.setOwnerId(ownerId);
        notification.setOwner(owner);
        notification.setContent(message);

        notification.setType(type);
        notification.setStatus(Notification.Status.UNREAD);
        notificationRepository.save(notification);
        return notification;
    }

    // Method to notify the owner of a post about a new like
    public void notifyPostOwner(Long ownerId, String message, Notification.NotificationType type) {
        sendNotification(ownerId, message, type);
    }

    // Method to notify the owner of a post about a new like
    public void notifyOwnerforchat(Long ownerId, String message, Notification.NotificationType type) {
        sendNotification(ownerId, message, type);
    }

    // Method to notify owners about a new group
    public void notifyOwnersForJoinGroup(List<Long> ownerIds, String message, Notification.NotificationType type) {
        for (Long ownerId : ownerIds) {
            sendNotification(ownerId, message, type);
        }
    }

    // Method to notify the owner of a comment about a new like
    public void notifyCommentOwner(Long ownerId, String message, Notification.NotificationType type) {
        sendNotification(ownerId, message, type);
    }

    @Transactional(readOnly = true)
    public List<Notification> getNotificationsForUser(Long userId) {
        logger.info("Retrieving notifications for user with ID {}", userId);
        Optional<Owner> ownerOptional = ownerRepository.findById(userId);
        Owner owner = ownerOptional.orElseThrow(() -> new OwnerNotFoundException("Owner not found with ID: " + userId));

        List<Notification> notifications = notificationRepository.findByOwnerOrderByNotificationCreationDateDesc(owner);
        logger.info("Retrieved {} notifications for user with ID {}", notifications.size(), userId);
        return notifications;
    }

    public Notification getnotificationById(Long notificationId) {
        logger.info("Retrieving notificationId  with id {}", notificationId);
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(
                        () -> new NotificationNotFoundException("notification not found with id: " + notificationId));
        logger.info("Retrieved notification chat with id {}", notificationId);
        return notification;
    }

    // Method to mark a notification as read

    public void markNotificationAsRead(Long notificationId) {

        logger.info("Retrieving Notification  with id {}", notificationId);
        Optional<Notification> notificationOptional = notificationRepository.findById(notificationId);
        Notification notification = notificationOptional.orElseThrow(
                () -> new NotificationNotFoundException("Notification not found with ID: " + notificationId));

        notification.setStatus(Notification.Status.READ);
        logger.info("Set Status as READ for the notification");
    }

    // Method to delete a notification
    public void deleteNotification(Long notificationId) {

        logger.info("Retrieving Notification  with id {}", notificationId);
        Optional<Notification> notificationOptional = notificationRepository.findById(notificationId);
        Notification notification = notificationOptional.orElseThrow(
                () -> new NotificationNotFoundException("Notification not found with ID: " + notificationId));

        notificationRepository.delete(notification);
        logger.info("delete the notification");
    }

    // Method to delete all notifications for a specific user
    public void deleteAllNotifications(Long ownerId) {

        logger.info("Retrieving owner  with id {}", ownerId);
        Optional<Owner> ownerOptional = ownerRepository.findById(ownerId);
        Owner owner = ownerOptional
                .orElseThrow(() -> new OwnerNotFoundException("Owner not found with ID: " + ownerId));

        List<Notification> notifi = owner.getNotifications();
        notificationRepository.deleteAll(notifi);

    }
}