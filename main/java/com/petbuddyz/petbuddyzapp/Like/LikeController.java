package com.petbuddyz.petbuddyzapp.Like;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import com.petbuddyz.petbuddyzapp.Comment.CommentNotFoundException;
import com.petbuddyz.petbuddyzapp.Security.services.UserDetailsImpl;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * The controller class that handles HTTP requests related to likes.
 */
@RestController
@RequestMapping
public class LikeController {
    @Autowired
    private final LikeService likeService;
    @Autowired
    private final LikeModelAssembler likeModelAssembler;

    private static final Logger logger = LoggerFactory.getLogger(LikeController.class);

    public LikeController(LikeService likeService, LikeModelAssembler likeModelAssembler) {
        this.likeService = likeService;
        this.likeModelAssembler = likeModelAssembler;
    }

    @GetMapping("/likes")
    public CollectionModel<EntityModel<Like>> all() throws LikeNotFoundException {
        logger.info("Getting Likes ");
        try {
            // Retrieve likes from the service
            Collection<EntityModel<Like>> likes = likeService.getAllLikes().getContent();
            return CollectionModel.of(likes,
                    linkTo(methodOn(LikeController.class).all()).withSelfRel());
        } catch (LikeNotFoundException ex) {

            logger.error("Error while getting likes: {}", ex.getMessage());
            throw ex;
        }
    }

    @GetMapping("/{likeId}")
    public EntityModel<Like> one(@PathVariable Long likeId) throws LikeNotFoundException {
        logger.info("Getting a Like ");
        try {
            // Retrieve likes from the service in a safe manner
            Like like = likeService.getLikeById(likeId)
                    .getContent(); // Assuming getContent() retrieves the Like object

            if (like == null) {
                throw new LikeNotFoundException("Like not found with ID:" + likeId);
            }

            return likeModelAssembler.toModel(like);
        } catch (LikeNotFoundException ex) {
            // Log the specific exception message
            logger.error("Like not found with ID {}: {}", likeId, ex.getMessage());
            throw ex;
        }
    }

    @PostMapping("/posts/{postid}/like")
    public ResponseEntity<?> likePost(@PathVariable Long postid)
            throws LikeNotFoundException {
        logger.info("Adding a like on a post ");
        Long ownerId = getAuthenticatedOwnerId();
        
        // Check if the owner has already liked the post
        if (likeService.hasLikedPost(ownerId, postid)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("You have already liked this post");
        }
        
        try {
            Like savedLike = likeService.addLikeOnPost(ownerId, postid);
            
            return ResponseEntity.ok(likeModelAssembler.toModel(savedLike));

        } catch (LikeNotFoundException ex) {
            logger.error("Error while adding a like on a post: {}", ex.getMessage());
            throw ex;
        }
    }


    @DeleteMapping("/posts/{postid}/like")
    public ResponseEntity<?> deleteLikePost(@PathVariable Long postid) {
        logger.info("Deleting a like from a post ");
        Long ownerId = getAuthenticatedOwnerId();
        
        // Check if the owner has not liked the post
        if (!likeService.hasLikedPost(ownerId, postid)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("You have not liked this post");
        }
        
        try {
            likeService.deleteLikeFromPost(ownerId, postid);
            return ResponseEntity.noContent().build();
        } catch (LikeNotFoundException ex) {
            logger.error("Error while deleting a like from a post: {}", ex.getMessage());
            throw ex;
        }
    }

    @GetMapping("/posts/{postId}/comments/{commentId}/like")
    public ResponseEntity<?> hasLikedComment(@PathVariable Long postId, @PathVariable Long commentId) {
        logger.info("Checking if the owner has liked the comment");
        Long ownerId = getAuthenticatedOwnerId();
        
        // Check if the owner has already liked the comment
        if (likeService.hasLikedComment(ownerId, commentId)) {
            return ResponseEntity.ok("The owner has already liked this comment");
        } else {
            return ResponseEntity.ok("The owner has not liked this comment");
        }
    }

    @PostMapping("/posts/{postId}/comments/{commentId}/like")
    public ResponseEntity<?> likeComment(@PathVariable Long postId, @PathVariable Long commentId)
            throws CommentNotFoundException {
        logger.info("Adding a like on a comment ");
        Long ownerId = getAuthenticatedOwnerId();
        
        // Check if the owner has already liked the comment
        if (likeService.hasLikedComment(ownerId, commentId)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("You have already liked this comment");
        }
        
        try {
            Like savedLike = likeService.addLikeOnComment(ownerId, commentId);
            return ResponseEntity.ok(likeModelAssembler.toModel(savedLike));
        } catch (CommentNotFoundException ex) {
            logger.error("Error while adding a like on a comment: {}", ex.getMessage());
            throw ex;
        }
    }


    @DeleteMapping("/posts/{postId}/comments/{commentId}/like")
    public ResponseEntity<?> deleteLikeComment(@PathVariable Long postId, @PathVariable Long commentId) {
        logger.info("Deleting a like from a comment ");
        try {
            Long ownerId = getAuthenticatedOwnerId();
            likeService.deleteLikeOnComment(ownerId, commentId);
            return ResponseEntity.noContent().build();
        } catch (Exception ex) {
            logger.error("Error while deleting a like from a comment: {}", ex.getMessage());
            throw ex;
        }
    }

    private Long getAuthenticatedOwnerId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userDetails.getId();
    }
}