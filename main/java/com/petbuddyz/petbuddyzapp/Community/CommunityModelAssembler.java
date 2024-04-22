package com.petbuddyz.petbuddyzapp.Community;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class CommunityModelAssembler implements RepresentationModelAssembler<Community, EntityModel<Community>>{

    @Override
    public EntityModel<Community> toModel(Community community) {
        return EntityModel.of(community,
            linkTo(methodOn(CommunityController.class).getCommunity(community.getCommunityId())).withSelfRel(),
            linkTo(methodOn(CommunityController.class).getAllCommunities()).withRel("communities")
        );
    }
}
