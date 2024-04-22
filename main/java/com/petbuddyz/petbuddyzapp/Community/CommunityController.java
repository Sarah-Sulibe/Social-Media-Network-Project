package com.petbuddyz.petbuddyzapp.Community;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.petbuddyz.petbuddyzapp.Owner.Owner;
import com.petbuddyz.petbuddyzapp.Owner.OwnerModelAssembler;
import com.petbuddyz.petbuddyzapp.Owner.OwnerNotFoundException;
import com.petbuddyz.petbuddyzapp.Owner.OwnerService;
import com.petbuddyz.petbuddyzapp.Post.Post;
import com.petbuddyz.petbuddyzapp.Post.PostModelAssembler;
import com.petbuddyz.petbuddyzapp.Security.services.UserDetailsImpl;
import com.petbuddyz.petbuddyzapp.Community.CommunityDto;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/communities")
public class CommunityController {

    private static final Logger logger = LogManager.getLogger(CommunityController.class);

    @Autowired
    private CommunityService communityService;

    @Autowired
    private CommunityModelAssembler communityModelAssembler;

    @Autowired
    private OwnerModelAssembler ownerModelAssembler;

    @Autowired
    private  OwnerService ownerService;

    @Autowired
    private  PostModelAssembler postModelAssembler;


    public CommunityController(CommunityService communityService, CommunityModelAssembler communityModelAssembler) {
        this.communityService = communityService;
        this.communityModelAssembler = communityModelAssembler;
    }


    public CommunityController(CommunityService communityService, CommunityModelAssembler communityModelAssembler,
    OwnerModelAssembler ownerModelAssembler) {
        this.communityService = communityService;
        this.communityModelAssembler = communityModelAssembler;
        this.ownerModelAssembler = ownerModelAssembler;
    }


    public CommunityController(CommunityService communityService, CommunityModelAssembler communityModelAssembler,
            OwnerService ownerService) {
        this.communityService = communityService;
        this.communityModelAssembler = communityModelAssembler;
        this.ownerService = ownerService;
    }

    public CommunityController() {
this.communityService = null;
this.communityModelAssembler = null;
this.ownerService = null;

}

    public Long getAuthenticatedOwnerId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userDetails.getId();
    }

    @GetMapping
    public CollectionModel<EntityModel<Community>> getAllCommunities() {
        logger.info("Getting all communities");
        List<EntityModel<Community>> communities = communityService.getAllCommunities().stream()
                .map(communityModelAssembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(communities,
                linkTo(methodOn(CommunityController.class).getAllCommunities()).withSelfRel());
    }

    @GetMapping("/{communityid}")
    public EntityModel<Community> getCommunity(@PathVariable Long communityid) {
        logger.info("Getting community with id: " + communityid);
        Community community = communityService.getCommunity(communityid);
        return communityModelAssembler.toModel(community);
    }

    @PostMapping
    public ResponseEntity<Community> createCommunity(@RequestBody CommunityDto CommunityDto) {
        logger.info("Creating community");
        Long ownerId = getAuthenticatedOwnerId();
        Owner owner = ownerService.getOwner(ownerId);
        if (owner == null) {
            logger.error("Owner not found with ID: {}", ownerId);
            throw new OwnerNotFoundException("Owner not found with ID: " + ownerId);
        }

        Community community = new Community();
        community.setCommunityName(CommunityDto.getCommunityName());
        community.setCommunityDescription(CommunityDto.getCommunityDescription());
        community.setCommunityOwner(owner);

        community = communityService.createCommunity(community);

        logger.info("Community created successfully");
        return ResponseEntity.ok(community);
    }

    @GetMapping("/{communityid}/members")
    public CollectionModel<EntityModel<Owner>> getCommunityMembers(@PathVariable Long communityid) {
        logger.info("Getting members of community with id: " + communityid);
        List<EntityModel<Owner>> communityMembers = communityService.getCommunityMembers(communityid).stream()
                .map(ownerModelAssembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(communityMembers,
                linkTo(methodOn(CommunityController.class).getCommunityMembers(communityid)).withSelfRel());
    }

    @GetMapping("/{communityid}/posts")
    public CollectionModel<EntityModel<Post>> getCommunityPosts(@PathVariable Long communityid) {
        logger.info("Getting posts for community with ID: {}", communityid);
        List<EntityModel<Post>> posts = communityService.getCommunityPosts(communityid).stream()
                .map(postModelAssembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(posts,
                linkTo(methodOn(CommunityController.class).getCommunityPosts(communityid)).withSelfRel());
    }

    @DeleteMapping("/{communityid}")
    public ResponseEntity<?> deleteCommunity(@PathVariable Long communityid) {
        Long ownerId = getAuthenticatedOwnerId();
        Community community = communityService.getCommunity(communityid);
        if (community.getCommunityOwner().getOwnerId() != ownerId) {
            logger.error("Owner with ID: {} is not the owner of community with ID: {}", ownerId, communityid);
            throw new OwnerNotFoundException(
                    "Owner with ID: " + ownerId + " is not the owner of community with ID: " + communityid);
        }

        communityService.deleteCommunity(communityid);
        logger.info("Community deleted successfully");
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{communityid}")
    public ResponseEntity<Community> updateCommunity(@PathVariable Long communityid,
            @RequestBody CommunityDto communityDto) {
        Long ownerId = getAuthenticatedOwnerId();
        Community community = communityService.getCommunity(communityid);
        if (community.getCommunityOwner().getOwnerId() != ownerId) {
            logger.error("Owner with ID: {} is not the owner of community with ID: {}", ownerId, communityid);
            throw new OwnerNotFoundException(
                    "Owner with ID: " + ownerId + " is not the owner of community with ID: " + communityid);
        }

        community.setCommunityName(communityDto.getCommunityName());
        community.setCommunityDescription(communityDto.getCommunityDescription());
        community = communityService.createCommunity(community);

        logger.info("Community updated successfully");
        return ResponseEntity.ok(community);
    }
    @PostMapping("/{communityid}/follow")
    public ResponseEntity<?> followCommunity(@PathVariable Long communityid) {
        logger.info("Following community with ID: {}", communityid);
        Long ownerId = getAuthenticatedOwnerId();
        
        // Check if the owner already follows the community
        if (ownerService.isFollowingCommunity(ownerId, communityid)) {
            return ResponseEntity.badRequest().body("Owner already follows the community");
        }
        
        ownerService.followCommunity(ownerId, communityid);
        return ResponseEntity.ok("Community followed successfully");
    }

    @DeleteMapping("/{communityid}/follow")
    public ResponseEntity<?> unfollowCommunity(@PathVariable Long communityid) {
        logger.info("Unfollowing community with ID: {}", communityid);
        Long ownerId = getAuthenticatedOwnerId();
        
        // Check if the owner already unfollowed the community
        if (!ownerService.isFollowingCommunity(ownerId, communityid)) {
            return ResponseEntity.badRequest().body("Owner does not follow the community");
        }
        
        ownerService.unfollowCommunity(ownerId, communityid);
        return ResponseEntity.ok("Community unfollowed successfully");
    }
}