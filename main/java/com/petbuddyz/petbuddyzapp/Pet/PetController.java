package com.petbuddyz.petbuddyzapp.Pet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/pets")
public class PetController {

    private static final Logger logger = LogManager.getLogger(PetController.class);

    @Autowired
    private PetService petService;

    @Autowired
    private PetModelAssembler petModelAssembler;

   


    public PetController(PetService petService, PetModelAssembler petModelAssembler) {
        this.petService = petService;
        this.petModelAssembler = petModelAssembler;
    }


    @GetMapping
    public CollectionModel<EntityModel<Pet>> getAllPets() {
        logger.info("Getting all pets");
        List<EntityModel<Pet>> pets = petService.getAllPets().stream()
                .map(petModelAssembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(pets,
                linkTo(methodOn(PetController.class).getAllPets()).withSelfRel());
    }


    @GetMapping("/{petid}")
    public EntityModel<Pet> getPet(@PathVariable Long petid) {
        logger.info("Getting pet with ID: {}", petid);
        Pet pet = petService.getPet(petid);
        return petModelAssembler.toModel(pet);
    }

    @GetMapping("/owner/{ownerId}")
    public CollectionModel<EntityModel<Pet>> getPetsByOwner_OwnerId(@PathVariable Long ownerId) {
        logger.info("Getting pets by owner ID: {}", ownerId);
        List<EntityModel<Pet>> pets = petService.getPetsByOwnerId(ownerId).stream()
                .map(petModelAssembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(pets,
                linkTo(methodOn(PetController.class).getPetsByOwner_OwnerId(ownerId)).withSelfRel());
    }

}