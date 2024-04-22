package com.petbuddyz.petbuddyzapp.Post;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.petbuddyz.petbuddyzapp.Like.Like;
import com.petbuddyz.petbuddyzapp.Like.LikeModelAssembler;
import com.petbuddyz.petbuddyzapp.Security.services.UserDetailsImpl;

@RestController
@RequestMapping("/posts")
public class PostController {

    private static final Logger logger = LogManager.getLogger(PostController.class);

    @Autowired
    private PostModelAssembler postModelAssembler;

    @Autowired
    private PostService postService;

    @Autowired
    private LikeModelAssembler likeModelAssembler;

public PostController(PostService postService, PostModelAssembler postModelAssembler,LikeModelAssembler likeModelAssembler) {
        this.postService = postService;
        this.postModelAssembler = postModelAssembler;
        this.likeModelAssembler= likeModelAssembler;
    }
    private Long getAuthenticatedOwnerId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userDetails.getId();
    }

    @GetMapping("/{postId}")
    public EntityModel<Post> getPost(@PathVariable Long postId) {
        logger.info("Getting post with ID: " + postId);
        Post post = postService.getPost(postId);
        return postModelAssembler.toModel(post);
    }

    @PutMapping("/{postId}")
    public ResponseEntity<Post> updatePost(@PathVariable Long postId, @RequestBody Map<String, String> requestBody) {
        logger.info("Updating post with ID: " + postId);
        Long ownerId = getAuthenticatedOwnerId();
        String postContent = requestBody.get("postContent");
        if (postContent == null) {
            logger.error("Invalid request body for updating post with ID: " + postId);
            return ResponseEntity.badRequest().build();
        }
        Post post = postService.updatePost(postId, postContent, ownerId);
        return ResponseEntity.ok(post);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId) {
        logger.info("Deleting post with ID: " + postId);
        Long ownerId = getAuthenticatedOwnerId();
        postService.deletePost(ownerId, postId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public CollectionModel<EntityModel<Post>> getAllPosts() {
        logger.info("Getting all posts");
        List<EntityModel<Post>> posts = postService.getAllPosts().stream()
                .map(postModelAssembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(posts,
                linkTo(methodOn(PostController.class).getAllPosts()).withSelfRel());
    }

    // TODO: continue this controller

    @GetMapping("/{postId}/likes")
    public CollectionModel<EntityModel<Like>> getLikes(@PathVariable Long postId) {
        logger.info("Getting likes for post with ID: " + postId);
        List<EntityModel<Like>> likes = postService.getPostLikes(postId).stream()
                .map(likeModelAssembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(likes,
                linkTo(methodOn(PostController.class).getLikes(postId)).withSelfRel());
    }

}