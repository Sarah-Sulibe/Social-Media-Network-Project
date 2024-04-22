package com.petbuddyz.petbuddyzapp.Comment;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.petbuddyz.petbuddyzapp.Exception.UnauthorizedOperationExecption;
import com.petbuddyz.petbuddyzapp.Security.services.UserDetailsImpl;

@RestController
@RequestMapping
public class CommentController {


    private static final Logger logger = LogManager.getLogger(CommentController.class);

    @Autowired
    private CommentService commentService;

    @Autowired
    private CommentModelAssembler commentModelAssembler;


    
    public CommentController(CommentService commentService, CommentModelAssembler commentModelAssembler) {
        this.commentService = commentService;
        this.commentModelAssembler = commentModelAssembler;
    }

    public Long getAuthenticatedOwnerId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userDetails.getId();
    }

    @GetMapping("posts/{postId}/comments/{commentid}")
    public EntityModel<Comment> getComment(@PathVariable Long commentid) {
        try {
            Comment comment = commentService.getComment(commentid);
            return commentModelAssembler.toModel(comment);
        } catch (CommentNotFoundException e) {
            logger.error("Failed to get comment with ID: " + commentid, e);
            return null;
        }
    }

    @GetMapping("posts/{postId}/comments")
    public CollectionModel<EntityModel<Comment>> getPostComments(@PathVariable Long postId) {
        List<Comment> comments = commentService.getPostComments(postId);
        return commentModelAssembler.toCollectionModel(comments);
    }

    @PostMapping("posts/{postId}/comments")
    public EntityModel<Comment> createComment(@PathVariable Long postId, @RequestBody CommentDto comment) {
        Long ownerId = getAuthenticatedOwnerId();
        Comment newComment = commentService.createPostComment(postId, comment, ownerId);
        return commentModelAssembler.toModel(newComment);
    }

    @DeleteMapping("posts/{postId}/comments/{commentId}")
    public ResponseEntity<?> deletePostComment(@PathVariable Long postId, @PathVariable Long commentId)
            throws CommentNotFoundException {
        Long ownerId = getAuthenticatedOwnerId();

        try {
            commentService.deletePostComment(postId, commentId, ownerId);
            return ResponseEntity.noContent().build();
        } catch (UnauthorizedOperationExecption e) {
            logger.error("Unauthorized operation: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (CommentNotFoundException e) {
            logger.error("Comment not found with ID: " + commentId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

}