package com.petbuddyz.petbuddyzapp.Like;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Service;

import com.petbuddyz.petbuddyzapp.Comment.Comment;
import com.petbuddyz.petbuddyzapp.Comment.CommentNotFoundException;
import com.petbuddyz.petbuddyzapp.Comment.CommentRepository;
import com.petbuddyz.petbuddyzapp.Notification.NotificationService;
import com.petbuddyz.petbuddyzapp.Owner.Owner;
import com.petbuddyz.petbuddyzapp.Owner.OwnerNotFoundException;
import com.petbuddyz.petbuddyzapp.Owner.OwnerRepository;
import com.petbuddyz.petbuddyzapp.Post.Post;
import com.petbuddyz.petbuddyzapp.Post.PostNotFoundException;
import com.petbuddyz.petbuddyzapp.Post.PostRepository;

import com.petbuddyz.petbuddyzapp.Notification.Notification;

/**
 * The LikeService class is responsible for handling operations related to
 * likes.
 * It provides methods for creating, retrieving, and deleting likes on posts and
 * comments.
 */
@Service
public class LikeService {

    private static final Logger logger = LoggerFactory.getLogger(LikeService.class);

    @Autowired
    private LikeRepository repository;

    @Autowired
    private LikeModelAssembler assembler;

    @Autowired
    private OwnerRepository ownerRepository; // Inject OwnerRepository here

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private NotificationService notificationService;

    public Like createLike(Long ownerId, Long postId, Long commentId) throws CommentNotFoundException {
        logger.info("Creating a new Like instance");

        // Retrieve owner and post from their respective repositories
        Owner owner = ownerRepository.findById(ownerId)
                .orElseThrow(() -> new OwnerNotFoundException("Owner not found with ID: " + ownerId));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found with ID: " + postId));

        // Create a new Like instance
        Like like = new Like();
        like.setOwner(owner);
        like.setPost(post);

        if (commentId != null) {

            Comment comment = commentRepository.findById(commentId)
                    .orElseThrow(() -> new CommentNotFoundException("Comment not found with ID: " + commentId));
            like.setComment(comment);
        }

        logger.info("Like instance created successfully");
        return like;
    }

    // get all likes
    public CollectionModel<EntityModel<Like>> getAllLikes() throws LikeNotFoundException {
        logger.info("Getting Likes ");
        try {
            // Retrieve likes from the repository
            List<Like> likes = repository.findAll();

            // Validate and convert likes to EntityModels using the assembler
            List<EntityModel<Like>> entityModels = likes.stream()
                    .filter(like -> like != null) // Optional: Ensure like objects are not null
                    .map(like -> assembler.toModel(like))
                    .collect(Collectors.toList());

            // Create and return the CollectionModel
            return CollectionModel.of(entityModels,
                    linkTo(methodOn(LikeController.class).all()).withSelfRel());
        } catch (LikeNotFoundException ex) {
            // Log any exceptions that might occur during the retrieval process
            logger.error("Error while getting likes: {}", ex.getMessage());
            throw ex; // Re-throw the exception to the caller
        }
    }

    // get one like

    public EntityModel<Like> getLikeById(Long likeId) throws LikeNotFoundException {
        logger.info("Getting a Like ");
        try {

            // Retrieve likes from the repository in a safe manner
            Like like = repository.findByLikeId(likeId)
                    .orElseThrow(() -> new LikeNotFoundException("Like not found with ID:" + likeId));

            return assembler.toModel(like);
        }

        catch (LikeNotFoundException ex) {
            // Log the specific exception message
            logger.error("Like not found with ID {}: {}", likeId, ex.getMessage());
            throw ex;
        }
    }

    public Like addLikeOnPost(Long ownerId, Long postId) {
        logger.info("Getting owner with ID: {}, and Getting post with ID: {}", ownerId, postId);

        // Fetch owner
        Owner owner = ownerRepository.findById(ownerId)
                .orElseThrow(() -> new OwnerNotFoundException("Owner not found with ID: " + ownerId));

        // Fetch post
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found with id: " + postId));

        // Create the like
        Like like = new Like();
        like.setOwner(owner);
        like.setPost(post);

        // Persist the like
        Like savedLike = repository.save(like);

        // Add the like to the post
        post.getPostLikes().add(savedLike);

        // Save the post (if needed)
        postRepository.save(post);

        // Notify the post owner about the new like
        logger.info("Notifying the post owner about the new like");
        notificationService.notifyPostOwner(post.getOwner().getOwnerId(), "New like on your post",
                Notification.NotificationType.POST_LIKE);

        logger.info("return the like");
        return savedLike;
    }

    public void deleteLikeFromPost(Long ownerId, Long postId) {
        logger.info("Deleting like from post. Owner ID: {}, Post ID: {}", ownerId, postId);
        try {
            // Find the like associated with the given owner and post
            Like like = repository.findByOwner_OwnerIdAndPost_PostId(ownerId, postId)
                    .orElseThrow(() -> new LikeNotFoundException(
                            "Like not found for owner ID: " + ownerId + " and post ID: " + postId));

            // Delete the like
            repository.delete(like);
            logger.info("Like deleted successfully from post. Like ID: {}", like.getLikeId());
        } catch (LikeNotFoundException e) {
            logger.error("Failed to delete like from post: {}", e.getMessage());
            throw e;
        }
    }

    // like comment
    public Like addLikeOnComment(Long ownerId, Long commentId) throws CommentNotFoundException {
        logger.info("Adding like on comment. Owner ID: {}, Comment ID: {}", ownerId, commentId);
        try {
            // Retrieve the owner and comment entities
            Owner owner = ownerRepository.findById(ownerId)
                    .orElseThrow(() -> new OwnerNotFoundException("Owner not found with ID: " + ownerId));

            Comment comment = commentRepository.findById(commentId)
                    .orElseThrow(() -> new CommentNotFoundException("Comment not found with ID: " + commentId));

            // Create a new like entity
            Like like = new Like();
            like.setOwner(owner);
            like.setComment(comment);

            comment.getCommentLikers().add(like.getOwner());
            // Save the like entity to the database
            Like savedLike = repository.save(like);

            logger.info("Like added successfully on comment. Like ID: {}", savedLike.getLikeId());

            // Notify the comment owner about the new like
            logger.info("Notifying the comment owner about the new like");
            notificationService.notifyPostOwner(comment.getOwner().getOwnerId(), "New like on your comment",
                    Notification.NotificationType.COMMENT_LIKE);
            return savedLike;
        } catch (CommentNotFoundException e) {
            logger.error("Failed to add like on comment: {}", e.getMessage());
            throw e;
        }
    }

    // delete like on a comment
    public void deleteLikeOnComment(Long ownerId, Long commentId) {
        logger.info("Delete a like from a comment ");

        Like like = repository.findByOwner_OwnerIdAndComment_CommentId(ownerId, commentId)
                .orElseThrow(() -> new LikeNotFoundException(
                        "Like not found for owner ID: " + ownerId + " and comment ID: " + commentId));

        // Delete the existing like
        repository.delete(like);
        logger.info("Like deleted successfully");
    }

    public boolean hasLikedPost(Long ownerId, Long postid) {
        logger.info("Checking if owner has liked the post. Owner ID: {}, Post ID: {}", ownerId, postid);
        return repository.existsByOwner_OwnerIdAndPost_PostId(ownerId, postid);
    }

    public boolean hasLikedComment(Long ownerId, Long commentId) {
        logger.info("Checking if owner has liked the comment. Owner ID: {}, Comment ID: {}", ownerId, commentId);
        return repository.existsByOwner_OwnerIdAndComment_CommentId(ownerId, commentId);
    }

}
