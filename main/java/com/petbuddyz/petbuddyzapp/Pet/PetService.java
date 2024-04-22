package com.petbuddyz.petbuddyzapp.Pet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PetService {

    private static final Logger logger = LogManager.getLogger(PetService.class);

    @Autowired
    private PetRepository petRepository;

    public List<Pet> getAllPets() {
        logger.info("Getting all pets");
        return petRepository.findAll();
    }

    public Pet getPet(Long id) {
        logger.info("Getting pet with id: {}", id);
        return petRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Pet with id {} not found", id);
                    return new PetNotFoundException("Pet not found");
                });
    }

    public Pet createPet(Pet newPet) {
        logger.info("Creating a new pet");
        return petRepository.save(newPet);
    }

    public Pet updatePet(Pet pet) {
        logger.info("Updating pet with id: {}", pet.getPetId());
        return petRepository.save(pet);
    }

    public void deletePet(Long id) {
        logger.info("Deleting pet with id: {}", id);
        petRepository.deleteById(id);
    }

    public List<Pet> getPetsByOwnerId(Long ownerId) {
        logger.info("Getting pets by owner id: {}", ownerId);
        return petRepository.findByOwner_OwnerId(ownerId);
    }

    public boolean isPetOwnedByOwner(Long petId, Long ownerId) {
        logger.info("Checking if pet with id {} is owned by owner with id {}", petId, ownerId);
        return petRepository.existsByPetIdAndOwner_OwnerId(petId, ownerId);
    }
}
