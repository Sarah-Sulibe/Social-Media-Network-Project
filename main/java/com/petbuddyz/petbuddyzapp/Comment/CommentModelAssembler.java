package com.petbuddyz.petbuddyzapp.Comment;

import org.springframework.stereotype.Component;

import com.petbuddyz.petbuddyzapp.Owner.OwnerController;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class CommentModelAssembler implements RepresentationModelAssembler<Comment, EntityModel<Comment>> {

    @Override
    public EntityModel<Comment> toModel(Comment comment) {
        return EntityModel.of(comment,
                linkTo(methodOn(OwnerController.class).getOwnerById(comment.getOwner().getOwnerId())).withRel("owner"),
                linkTo(methodOn(CommentController.class).getComment(comment.getCommentId())).withSelfRel(),
                linkTo(methodOn(CommentController.class).getPostComments(comment.getPost().getPostId())).withRel("comments"));
    }

}
