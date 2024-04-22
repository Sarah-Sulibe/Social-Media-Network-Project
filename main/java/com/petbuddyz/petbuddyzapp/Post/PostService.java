package com.petbuddyz.petbuddyzapp.Post;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.petbuddyz.petbuddyzapp.Comment.Comment;
import com.petbuddyz.petbuddyzapp.Community.CommunityNotFoundException;
import com.petbuddyz.petbuddyzapp.Community.CommunityRepository;
import com.petbuddyz.petbuddyzapp.Like.Like;
import com.petbuddyz.petbuddyzapp.Owner.OwnerNotFoundException;
import com.petbuddyz.petbuddyzapp.Owner.OwnerRepository;
import com.petbuddyz.petbuddyzapp.Pet.PetNotFoundException;
import com.petbuddyz.petbuddyzapp.Pet.PetRepository;

@Service
public class PostService {

    private static final Logger logger = LogManager.getLogger(PostService.class);

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private CommunityRepository communityRepository;

    @Autowired
    private PetRepository petRepository;

    /**
     * Creates a new post.
     *
     * @param postDTO The post data transfer object.
     * @return The created post.
     */
    public Post createPost(PostDTO postDTO, Long ownerId) {
        validatePostDTO(postDTO);

        logger.info("Creating new post");

        Post post = new Post();
        post.setPostContent(postDTO.getPostContent());
        post.setPostMedia(postDTO.getPostMedia());
        post.setOwner(ownerRepository.findById(ownerId)
                .orElseThrow(() -> new OwnerNotFoundException("Owner not found")));
        post.setPet(petRepository.findById(postDTO.getPetId())
                .orElseThrow(() -> new PetNotFoundException("Pet not found")));

        if (postDTO.getCommunityId() != null) {
            post.setCommunity(communityRepository.findById(postDTO.getCommunityId())
                    .orElseThrow(() -> new CommunityNotFoundException("Community not found")));
        }

        post.setPostCreationDate(new Date());
        post.setPostUpdateDate(new Date());

        logger.info("Post created successfully");

        return postRepository.save(post);
    }

    /**
     * Updates an existing post.
     *
     * @param postDTO The post data transfer object.
     * @return The updated post.
     */
    public Post updatePost(Long postId, String postContent, Long ownerId) {
        if (postId == null) {
            throw new PostNotFoundException("Post ID cannot be null");
        }

        if (postContent == null || postContent.isEmpty()) {
            throw new IllegalArgumentException("Post content cannot be empty");
        }

        // owner can update only their own posts
        Optional<Post> post = postRepository.findById(postId);
        if (post.isPresent() && post.get().getOwner().getOwnerId().equals(ownerId)) {
            logger.info("Updating post with ID: {}", postId);
            post.get().setPostContent(postContent);
            post.get().setPostUpdateDate(new Date());
            logger.info("Post updated successfully");
            return postRepository.save(post.get());
        } else {
            throw new PostNotFoundException("Post not found");
        }
    }

    /**
     * Deletes a post by its ID.
     *
     * @param postId The ID of the post to delete.
     */
    public void deletePost(Long ownerId, Long postId) {
        if (postId == null) {
            throw new PostNotFoundException("Post ID cannot be null");
        }

        // owner can delete only their own posts
        Optional<Post> post = postRepository.findById(postId);
        if (post.isPresent() && post.get().getOwner().getOwnerId().equals(ownerId)) {
            logger.info("Deleting post with ID: {}", postId);
            postRepository.deleteById(postId);
            logger.info("Post deleted successfully");
        } else {
            throw new PostNotFoundException("Post not found");
        }

    }

    /**
     * Retrieves all posts.
     *
     * @return A list of all posts.
     */
    public List<Post> getAllPosts() {
        logger.info("Retrieving all posts");
        return postRepository.findAll();
    }

    // Helper methods

    private void validatePostDTO(PostDTO postDTO) {
        if (postDTO == null) {
            throw new IllegalArgumentException("Post DTO cannot be null");
        }

        if (postDTO.getPostContent() == null || postDTO.getPostContent().isEmpty()) {
            throw new IllegalArgumentException("Post content cannot be empty");
        }

        // Add more validation rules if needed
    }

    public Post getPost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found"));
    }

    public List<Comment> getPostComments(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found"));
        return post.getComments();
    }

    public List<Like> getPostLikes(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found"));
        return post.getPostLikes();
    }

}
