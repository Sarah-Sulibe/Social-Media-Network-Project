package com.petbuddyz.petbuddyzapp.Post;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
    
    
    List<Post> findByPet_PetId(Long petId);

    List<Post> findByOwner_OwnerId(Long ownerId);

    List<Post> findByCommunity_CommunityId(Long communityId);
    
}