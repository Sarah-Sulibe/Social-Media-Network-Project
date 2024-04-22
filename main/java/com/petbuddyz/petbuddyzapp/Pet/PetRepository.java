package com.petbuddyz.petbuddyzapp.Pet;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetRepository extends JpaRepository<Pet, Long> {
    List<Pet> findByOwner_OwnerId(Long ownerId);

    boolean existsByPetIdAndOwner_OwnerId(Long petId, Long ownerId);

}