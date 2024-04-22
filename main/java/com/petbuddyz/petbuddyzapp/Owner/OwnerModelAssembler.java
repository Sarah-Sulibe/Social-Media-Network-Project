package com.petbuddyz.petbuddyzapp.Owner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.petbuddyz.petbuddyzapp.Pet.PetController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class OwnerModelAssembler implements RepresentationModelAssembler<Owner, EntityModel<Owner>> {

    private static final Logger logger = LoggerFactory.getLogger(OwnerService.class);

    @Override
    public EntityModel<Owner> toModel(Owner owner) {
        logger.info("Converting owner to model");
        return EntityModel.of(owner,
                linkTo(methodOn(OwnerController.class).getOwnerById(owner.getOwnerId())).withSelfRel(),
                linkTo(methodOn(OwnerController.class).getAllOwners()).withRel("owners"),
                linkTo(methodOn(PetController.class).getPetsByOwner_OwnerId(owner.getOwnerId())).withRel("pets"),
                linkTo(methodOn(OwnerController.class).getFollowers(owner.getOwnerId())).withRel("followers"),
                linkTo(methodOn(OwnerController.class).getFollowing(owner.getOwnerId())).withRel("following"));
    }

}
