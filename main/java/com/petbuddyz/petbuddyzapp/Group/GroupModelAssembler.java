package com.petbuddyz.petbuddyzapp.Group;




import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.petbuddyz.petbuddyzapp.Like.LikeModelAssembler;

/**
 * The GroupModelAssembler class is responsible for converting a Group object into an EntityModel<Group> object,
 * which represents the group's data along with relevant links.
 */
@Component

public class GroupModelAssembler implements RepresentationModelAssembler<Group, EntityModel<Group>> {
  private static final Logger logger = LoggerFactory.getLogger(LikeModelAssembler.class);

     @Override
    public EntityModel<Group> toModel(Group group) {
  
    
     try {
        return EntityModel.of(group, //
                linkTo(methodOn(GroupController.class).getGroup(group.getGroupId())).withSelfRel(),
                linkTo(methodOn(GroupController.class).allGroups()).withRel("groups"));
    } catch (Exception e) {
        
        logger.error("Error while creating Like entity model: {}", e.getMessage());
        throw new RuntimeException("Error while creating Like entity model", e);
    }
}

}