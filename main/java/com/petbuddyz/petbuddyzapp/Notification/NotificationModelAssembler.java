package com.petbuddyz.petbuddyzapp.Notification;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The NotificationModelAssembler class is responsible for converting a Notification object into a
 * representation model that can be used for API responses. It implements the RepresentationModelAssembler
 * interface and provides the necessary methods to create the entity model.
 */
@Component
public class NotificationModelAssembler
    implements RepresentationModelAssembler<Notification, EntityModel<Notification>> {
  private static final Logger logger = LoggerFactory.getLogger(NotificationModelAssembler.class);

  @Override
  public EntityModel<Notification> toModel(Notification notification) {
    try {
      return EntityModel.of(notification, //
          linkTo(methodOn(NotificationController.class).getNotificationById(notification.getNotificationId()))
              .withSelfRel(),
          linkTo(methodOn(NotificationController.class).getNotifications(notification.getOwner().getOwnerId()))
              .withRel("notifications"));
    } catch (Exception e) {

      logger.error("Error while creating Notification entity model: {}", e.getMessage());
      throw new RuntimeException("Error while creating Notification entity model", e);
    }
  }

}
