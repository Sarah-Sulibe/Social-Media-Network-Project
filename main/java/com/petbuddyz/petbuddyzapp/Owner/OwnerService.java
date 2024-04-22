package com.petbuddyz.petbuddyzapp.Owner;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.petbuddyz.petbuddyzapp.Community.Community;
import com.petbuddyz.petbuddyzapp.Community.CommunityRepository;
import com.petbuddyz.petbuddyzapp.Pet.Pet;
import com.petbuddyz.petbuddyzapp.Pet.PetRepository;
import com.petbuddyz.petbuddyzapp.Post.Post;
import com.petbuddyz.petbuddyzapp.Post.PostRepository;

@Service
public class OwnerService {

    private static final Logger logger = LoggerFactory.getLogger(OwnerService.class);

    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private CommunityRepository communityRepository;

    @Autowired
    private PostRepository postRepository;

    // Get Owner
    public Owner getOwner(Long ownerId) {
        logger.info("Getting owner with ID: {}", ownerId);
        Optional<Owner> optionalOwner = ownerRepository.findById(ownerId);
        return optionalOwner.orElseThrow(() -> new RuntimeException("Owner not found with ID: " + ownerId));
    }

    // Update Owner
    public void updateOwner(OwnerUpdateDto ownerUpdateDto, Long ownerId) {
        logger.info("Updating owner with ID: {}", ownerId);
        Optional<Owner> optionalOwner = ownerRepository.findById(ownerId);
        if (optionalOwner.isPresent()) {
            Owner owner = optionalOwner.get();
            owner.setOwnerName(ownerUpdateDto.getOwnerName());
            owner.setOwnerPicture(ownerUpdateDto.getOwnerPicture());
            owner.setOwnerBio(ownerUpdateDto.getOwnerBio());
            ownerRepository.save(owner);
            logger.info("Owner updated successfully");
        } else {
            throw new RuntimeException("Owner not found with ID: " + ownerId);
        }
    }

    // Delete Owner
    public void deleteOwner(Long ownerId) {
        logger.info("Deleting owner with ID: {}", ownerId);
        Optional<Owner> optionalOwner = ownerRepository.findById(ownerId);
        if (optionalOwner.isPresent()) {
            ownerRepository.deleteById(ownerId);
            logger.info("Owner deleted successfully");
        } else {
            throw new RuntimeException("Owner not found with ID: " + ownerId);
        }
    }

    // Get Owner's Pets
    public List<Pet> getOwnerPets(Long ownerId) {
        logger.info("Getting pets of owner with ID: {}", ownerId);
        return petRepository.findByOwner_OwnerId(ownerId);
    }

    // Get Pet's Posts
    public List<Post> getPetPosts(Long petId) {
        logger.info("Getting posts of pet with ID: {}", petId);
        return postRepository.findByPet_PetId(petId);
    }

    // Delete Pet
    public void deletePet(Long petId) {
        logger.info("Deleting pet with ID: {}", petId);
        Optional<Pet> optionalPet = petRepository.findById(petId);
        if (optionalPet.isPresent()) {
            petRepository.deleteById(petId);
            logger.info("Pet deleted successfully");
        } else {
            throw new RuntimeException("Pet not found with ID: " + petId);
        }
    }

    // TODO : add community related methods

    // Follow Community
    public void followCommunity(Long ownerId, Long communityId) {
        logger.info("Following community with ID: {} for owner with ID: {}", communityId, ownerId);
        Optional<Owner> optionalOwner = ownerRepository.findById(ownerId);
        Optional<Community> optionalCommunity = communityRepository.findById(communityId);
        if (optionalOwner.isPresent() && optionalCommunity.isPresent()) {
            Owner owner = optionalOwner.get();
            Community community = optionalCommunity.get();
            owner.getCommunities().add(community);
            ownerRepository.save(owner);
            logger.info("Community followed successfully");
        } else {
            throw new RuntimeException("Owner or community not found with IDs: " + ownerId + " and " + communityId);
        }
    }

    // Unfollow Community
    public void unfollowCommunity(Long ownerId, Long communityId) {
        logger.info("Unfollowing community with ID: {} for owner with ID: {}", communityId, ownerId);
        Optional<Owner> optionalOwner = ownerRepository.findById(ownerId);
        Optional<Community> optionalCommunity = communityRepository.findById(communityId);
        if (optionalOwner.isPresent() && optionalCommunity.isPresent()) {
            Owner owner = optionalOwner.get();
            Community community = optionalCommunity.get();
            owner.getCommunities().remove(community);
            ownerRepository.save(owner);
            logger.info("Community unfollowed successfully");
        } else {
            throw new RuntimeException("Owner or community not found with IDs: " + ownerId + " and " + communityId);
        }
    }

    // Follow Owner
    public void followOwner(Long followerid, Long ownerid) {
        logger.info("Following owner with ID: {} for owner with ID: {}", ownerid, followerid);
        // the ownerid is the one getting followed and followerid is the one following
        Optional<Owner> optionalOwner = ownerRepository.findById(ownerid);
        Optional<Owner> optionalFollower = ownerRepository.findById(followerid);
        if (optionalOwner.isPresent() && optionalFollower.isPresent()) {
            Owner owner = optionalOwner.get();
            Owner follower = optionalFollower.get();
            owner.getFollowers().add(follower);
            follower.getFollowing().add(owner);
            ownerRepository.save(owner);
            logger.info("Owner followed successfully");
        } else {
            throw new RuntimeException("Owner or follower not found with IDs: " + ownerid + " and " + followerid);
        }
    }

    // Unfollow Owner
    public void unfollowOwner(Long followerid, Long ownerid) {
        logger.info("Unfollowing owner with ID: {} for owner with ID: {}", ownerid, followerid);
        // the ownerid is the one getting unfollowed and followerid is the one unfollowing
        Optional<Owner> optionalOwner = ownerRepository.findById(ownerid);
        Optional<Owner> optionalFollower = ownerRepository.findById(followerid);
        if (optionalOwner.isPresent() && optionalFollower.isPresent()) {
            Owner owner = optionalOwner.get();
            Owner follower = optionalFollower.get();
            owner.getFollowers().remove(follower);
            follower.getFollowing().remove(owner);
            ownerRepository.save(owner);
            logger.info("Owner unfollowed successfully");
        } else {
            throw new RuntimeException("Owner or follower not found with IDs: " + ownerid + " and " + followerid);
        }
    }

    public List<Owner> getAllOwners() {
        logger.info("Getting all owners");
        return ownerRepository.findAll();
    }

    public List<Owner> getFollowers(Long id) {
        return ownerRepository.getFollowers(id);
    }

    public List<Owner> getFollowing(Long id) {
        return ownerRepository.getFollowing(id);
    }

    public List<Post> getOwnerPosts(Long ownerId) {
        return postRepository.findByOwner_OwnerId(ownerId);
    }

    public boolean isFollowingOwner(Long followerid, Long ownerid) {
        Optional<Owner> optionalOwner = ownerRepository.findById(ownerid);
        Optional<Owner> optionalFollower = ownerRepository.findById(followerid);

        if (optionalOwner.isPresent() && optionalFollower.isPresent()) {
            Owner owner = optionalOwner.get();
            Owner follower = optionalFollower.get();
            return owner.getFollowers().contains(follower);
        } else {
            throw new RuntimeException("Owner or follower not found with IDs: " + ownerid + " and " + followerid);
        }
    }

    public boolean isOwnerMemberOfCommunity(Long ownerId, Long communityId) {
        Optional<Owner> optionalOwner = ownerRepository.findById(ownerId);
        Optional<Community> optionalCommunity = communityRepository.findById(communityId);

        if (optionalOwner.isPresent() && optionalCommunity.isPresent()) {
            Owner owner = optionalOwner.get();
            Community community = optionalCommunity.get();
            return owner.getCommunities().contains(community);
        } else {
            throw new RuntimeException("Owner or community not found with IDs: " + ownerId + " and " + communityId);
        } 
    }

    public boolean isFollowingCommunity(Long ownerId, Long communityid) {
        Optional<Owner> optionalOwner = ownerRepository.findById(ownerId);
        Optional<Community> optionalCommunity = communityRepository.findById(communityid);

        if (optionalOwner.isPresent() && optionalCommunity.isPresent()) {
            Owner owner = optionalOwner.get();
            Community community = optionalCommunity.get();
            return owner.getCommunities().contains(community);
        } else {
            throw new RuntimeException("Owner or community not found with IDs: " + ownerId + " and " + communityid);
        }
    }

}
