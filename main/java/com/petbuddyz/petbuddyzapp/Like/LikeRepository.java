package com.petbuddyz.petbuddyzapp.Like;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.petbuddyz.petbuddyzapp.Owner.Owner;

import java.util.List;
import java.util.Optional;

/**
 * The repository interface for managing Like entities.
 */
public interface LikeRepository extends JpaRepository<Like, Long> {

    /**
     * Retrieves a Like entity by the owner's ID and post's ID.
     *
     * @param ownerId The ID of the owner.
     * @param postId  The ID of the post.
     * @return An Optional containing the Like entity, or an empty Optional if not
     *         found.
     */
    Optional<Like> findByOwner_OwnerIdAndPost_PostId(Long ownerId, Long postId);

    /**
     * Retrieves a Like entity by the comment's ID.
     *
     * @param commentId The ID of the comment.
     * @return An Optional containing the Like entity, or an empty Optional if not
     *         found.
     */
    Optional<Like> findByComment_CommentId(Long commentId);

    /**
     * Retrieves a Like entity by the Like's ID.
     *
     * @param likeId The ID of the Like.
     * @return An Optional containing the Like entity, or an empty Optional if not
     *         found.
     */
    Optional<Like> findByLikeId(Long likeId);

    @Query("SELECT l FROM Like l WHERE l.owner.ownerId = :ownerId AND l.comment.commentId = :commentId")
    Optional<Like> findByOwner_OwnerIdAndComment_CommentId(Long ownerId, Long commentId);



    boolean existsByOwner_OwnerIdAndPost_PostId(Long ownerId, Long postid);

    boolean existsByOwner_OwnerIdAndComment_CommentId(Long ownerId, Long commentId);

}
