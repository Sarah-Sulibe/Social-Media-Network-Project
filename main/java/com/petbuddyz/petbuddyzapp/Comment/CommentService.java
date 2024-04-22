package com.petbuddyz.petbuddyzapp.Comment;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.petbuddyz.petbuddyzapp.Exception.UnauthorizedOperationExecption;
import com.petbuddyz.petbuddyzapp.Owner.Owner;
import com.petbuddyz.petbuddyzapp.Owner.OwnerNotFoundException;
import com.petbuddyz.petbuddyzapp.Owner.OwnerRepository;
import com.petbuddyz.petbuddyzapp.Post.Post;
import com.petbuddyz.petbuddyzapp.Post.PostNotFoundException;
import com.petbuddyz.petbuddyzapp.Post.PostRepository;

@Service
public class CommentService {

    private static final Logger logger = LogManager.getLogger(CommentService.class);

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private OwnerRepository ownerRepository;

    public List<Comment> getPostComments(Long postId) {
        logger.info("Getting comments for post with ID: " + postId);
        return commentRepository.findByPost_PostId(postId);
    }

    public Comment getComment(Long id) throws CommentNotFoundException {
        logger.info("Getting comment with ID: " + id);
        return commentRepository.findById(id)
                .orElseThrow(() -> new CommentNotFoundException("Comment not found"));
    }

    public Comment createPostComment(Long postId, CommentDto comment, Long ownerId) {
        logger.info("Creating comment for post with ID: " + postId);
        if (comment == null) {
            throw new IllegalArgumentException("Comment cannot be null");
        }
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found"));
        Owner owner = ownerRepository.findById(ownerId)
                .orElseThrow(() -> new OwnerNotFoundException("Owner not found"));
        Comment newComment = new Comment();
        newComment.setPost(post);
        newComment.setOwner(owner);
        newComment.setContent(comment.getContent());
        return commentRepository.save(newComment);
    }

    public void deletePostComment(Long postId, Long commentId, Long ownerId) throws CommentNotFoundException {
        logger.info("Deleting comment with ID: " + commentId);
        // owner can delete only their own comments
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Comment not found"));
        if (comment.getOwner().getOwnerId().equals(ownerId)) {
            commentRepository.deleteById(commentId);
        } else {
            throw new UnauthorizedOperationExecption("Owner is not authorized to delete this comment");
        }
    }
}
