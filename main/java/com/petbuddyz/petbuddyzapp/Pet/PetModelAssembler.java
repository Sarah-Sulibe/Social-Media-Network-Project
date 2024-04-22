package com.petbuddyz.petbuddyzapp.Pet;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class PetModelAssembler implements RepresentationModelAssembler<Pet, EntityModel<Pet>> {

    @Override
    public EntityModel<Pet> toModel(Pet pet) {
        return EntityModel.of(pet,
                linkTo(methodOn(PetController.class).getPet(pet.getPetId())).withSelfRel(),
                linkTo(methodOn(PetController.class).getAllPets()).withRel("pets"));

                //TODO: add linkto owner through the owner controller
    }
}