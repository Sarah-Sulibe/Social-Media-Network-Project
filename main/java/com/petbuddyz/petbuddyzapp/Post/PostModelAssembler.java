package com.petbuddyz.petbuddyzapp.Post;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class PostModelAssembler implements RepresentationModelAssembler<Post, EntityModel<Post>>{
    @Override
    public EntityModel<Post> toModel(Post post) {
        return EntityModel.of(post,
                linkTo(methodOn(PostController.class).getPost(post.getPostId())).withSelfRel(),
                linkTo(methodOn(PostController.class).getAllPosts()).withRel("posts")
                //TODO: add linkto likes and comments through the like controller
        );
    }
}
