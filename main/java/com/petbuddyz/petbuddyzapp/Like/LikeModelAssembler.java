package com.petbuddyz.petbuddyzapp.Like;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The LikeModelAssembler class is responsible for converting Like entities into
 * EntityModel objects.
 * It implements the RepresentationModelAssembler interface with Like as the
 * entity type and EntityModel<Like> as the model type.
 * This class provides methods to convert a single Like entity into an
 * EntityModel object and a collection of Like entities into a CollectionModel
 * object.
 */
@Component
public class LikeModelAssembler implements RepresentationModelAssembler<Like, EntityModel<Like>> {
    private static final Logger logger = LoggerFactory.getLogger(LikeModelAssembler.class);

    @Override
    public EntityModel<Like> toModel(Like like) {
        try {
            if (like == null) {
                throw new IllegalArgumentException("Like object is null");
            }
    
            return EntityModel.of(like, //
                    linkTo(methodOn(LikeController.class).one(like.getLikeId())).withSelfRel(),
                    linkTo(methodOn(LikeController.class).all()).withRel("likes"));
        } catch (Exception e) {
            logger.error("Error while creating Like entity model: {}", e.getMessage());
            throw new RuntimeException("Error while creating Like entity model", e);
        }
    }
    
}
