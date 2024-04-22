package com.petbuddyz.petbuddyzapp.Notification;

import java.util.List;
import java.util.Optional;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.petbuddyz.petbuddyzapp.Owner.Owner;
import com.petbuddyz.petbuddyzapp.Owner.OwnerRepository;
import com.petbuddyz.petbuddyzapp.Security.services.UserDetailsImpl;

/**
 * The NotificationController class handles HTTP requests related to notifications.
 * It provides endpoints for creating, sending, retrieving, and deleting notifications.
 */
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationModelAssembler notificationModelAssembler;
    private OwnerRepository ownerRepository;

    public NotificationController(NotificationService notificationService,
            NotificationModelAssembler notificationModelAssembler, OwnerRepository ownerRepository) {
        this.notificationService = notificationService;
        this.notificationModelAssembler = notificationModelAssembler;
        this.ownerRepository = ownerRepository;
    }

    private Long getAuthenticatedOwnerId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userDetails.getId();
    }

    @PostMapping("/create")
    public ResponseEntity<EntityModel<Notification>> createNotification(@RequestParam Long ownerId,
                                                                        @RequestParam String message,
                                                                        @RequestParam Notification.NotificationType type) {
     Long authenticatedOwnerId = getAuthenticatedOwnerId();                                                                  
        Notification createdNotification = notificationService.createNotification(authenticatedOwnerId, message);
        EntityModel<Notification> notificationEntityModel = notificationModelAssembler.toModel(createdNotification);
        return new ResponseEntity<>(notificationEntityModel, HttpStatus.CREATED);
    }

   
    @PostMapping("/send")
    public ResponseEntity<EntityModel<Notification>> sendNotification(@RequestParam Long ownerId,
                                                                       @RequestParam String message,
                                                                       @RequestParam Notification.NotificationType type) {
       Long authenticatedOwnerId = getAuthenticatedOwnerId();  
    Notification notification = notificationService.sendNotification(authenticatedOwnerId, message, type);
    EntityModel<Notification> model = notificationModelAssembler.toModel(notification);
        return new ResponseEntity<>(model, HttpStatus.CREATED);
    }

    @GetMapping("/{notificationId}")
    public ResponseEntity<EntityModel<Notification>> getNotificationById(@PathVariable Long notificationId) {
        Notification notificationOptional = notificationService.getnotificationById(notificationId);
        if (notificationOptional == null) {
            return ResponseEntity.notFound().build();
        }

        Notification notification = notificationOptional;
        EntityModel<Notification> model = notificationModelAssembler.toModel(notification);
        model.add(WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder.methodOn(NotificationController.class).getNotificationById(notificationId))
                .withSelfRel());
        return ResponseEntity.ok(model);
    }
   
  //url ?
    @GetMapping("/{userId}")
    public ResponseEntity<CollectionModel<EntityModel<Notification>>> getNotifications(@PathVariable Long userId) {
        Long authenticatedOwnerId = getAuthenticatedOwnerId();  
        List<Notification> notifications = notificationService.getNotificationsForUser(authenticatedOwnerId);
        return ResponseEntity.ok(notificationModelAssembler.toCollectionModel(notifications));
    }

    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Notification> deleteNotification(@PathVariable Long notificationId) {

        
       Notification notificationOptional = notificationService.getnotificationById(notificationId);
       if (notificationOptional==null) {
        return ResponseEntity.notFound().build();
    }

   
        notificationService.deleteNotification(notificationId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/owner/{ownerId}")
    public ResponseEntity<Notification> deleteAllNotifications(@PathVariable Long ownerId) {
        if (ownerId == null) {
            return ResponseEntity.badRequest().build();
        }

        Optional<Owner> ownerOptional = ownerRepository.findById(ownerId);
        if (!ownerOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        notificationService.deleteAllNotifications(ownerId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{notificationId}/read")
    public ResponseEntity<Void> markNotificationAsRead(@PathVariable Long notificationId) {
     Notification notificationOptional = notificationService.getnotificationById(notificationId);
     if (notificationOptional==null) {
        return ResponseEntity.notFound().build();
    }

        notificationService.markNotificationAsRead(notificationId);
        return ResponseEntity.ok().build();
    }

    

}