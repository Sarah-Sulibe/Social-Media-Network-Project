package com.petbuddyz.petbuddyzapp.Owner;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.petbuddyz.petbuddyzapp.Exception.CustomErrorResponse;
import com.petbuddyz.petbuddyzapp.FileUpload.FileUploadService;
import com.petbuddyz.petbuddyzapp.Group.UnauthorizedAccessException;
import com.petbuddyz.petbuddyzapp.Pet.Pet;
import com.petbuddyz.petbuddyzapp.Pet.PetDto;
import com.petbuddyz.petbuddyzapp.Pet.PetModelAssembler;
import com.petbuddyz.petbuddyzapp.Pet.PetService;
import com.petbuddyz.petbuddyzapp.Post.Post;
import com.petbuddyz.petbuddyzapp.Post.PostDTO;
import com.petbuddyz.petbuddyzapp.Post.PostModelAssembler;
import com.petbuddyz.petbuddyzapp.Post.PostRepository;
import com.petbuddyz.petbuddyzapp.Post.PostService;
import com.petbuddyz.petbuddyzapp.Security.services.UserDetailsImpl;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/owners")
public class OwnerController {

    private static final Logger logger = LogManager.getLogger(OwnerController.class);

    @Autowired
    private OwnerService ownerService;

    @Autowired
    private OwnerModelAssembler ownerModelAssembler;

    @Autowired
    private PetModelAssembler PetModelAssembler;

    @Autowired
    private PetService petService;

    @Autowired
    private PostService postService;

    @Autowired
    private PostModelAssembler postModelAssembler;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private FileUploadService fileUploadService;





    
    public OwnerController(OwnerService ownerService, OwnerModelAssembler ownerModelAssembler,
            com.petbuddyz.petbuddyzapp.Pet.PetModelAssembler petModelAssembler, PetService petService){        
        this.ownerService = ownerService;
        this.ownerModelAssembler = ownerModelAssembler;
       this.PetModelAssembler = petModelAssembler;
        this.petService = petService;
      
    
 
    }

        // Default constructor
        public OwnerController() {
            this.ownerService = null;
            this.petService = null;
            this.fileUploadService = null;
            this.postService = null;
        }
    
    // Constructor with dependencies
    public OwnerController(OwnerService ownerService, PetService petService, 
                           FileUploadService fileUploadService, PostService postService) {
        this.ownerService = ownerService;
        this.petService = petService;
        this.fileUploadService = fileUploadService;
        this.postService = postService;
    }
   

    public OwnerController(OwnerService ownerService2, PostModelAssembler postModelAssembler2) {
        //TODO Auto-generated constructor stub
        this.ownerService= ownerService2;
        this.postModelAssembler = postModelAssembler2;
    }

    public Long getAuthenticatedOwnerId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userDetails.getId();
    }

    @GetMapping
    public CollectionModel<EntityModel<Owner>> getAllOwners() {
        logger.info("Getting all owners");
        List<EntityModel<Owner>> owners = ownerService.getAllOwners().stream()
                .map(ownerModelAssembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(owners,
                linkTo(methodOn(OwnerController.class).getAllOwners()).withSelfRel());
    }

    @GetMapping("/{ownerid}")
    public EntityModel<Owner> getOwnerById(@PathVariable Long ownerid) {
        logger.info("Getting owner by ID: {}", ownerid);
        Owner owner = ownerService.getOwner(ownerid);
        try {
            return ownerModelAssembler.toModel(owner);
        } catch (Exception e) {
            logger.error("Owner not found with ID: {}", ownerid);
            throw new OwnerNotFoundException("Owner not found with ID: " + ownerid);
        }

    }

    @PutMapping("/{ownerid}")
    public ResponseEntity<?> updateOwner(@Valid @RequestPart("owner") OwnerUpdateDto ownerUpdateDto,
            @RequestPart("file") MultipartFile file, @PathVariable Long ownerid) {
        logger.info("Updating owner with ID: {}", ownerid);
        Long ownerId = getAuthenticatedOwnerId();
        if (!ownerId.equals(ownerid)) {
            throw new UnauthorizedAccessException("You are not authorized to update this owner.");
        }

        Owner owner = ownerService.getOwner(ownerid);
        if (owner == null) {
            logger.error("Owner not found with ID: {}", ownerid);
            throw new OwnerNotFoundException("Owner not found with ID: " + ownerid);
        }

        // Handle file upload here
        if (file != null) {
            // Save the file
            String fileName = fileUploadService.saveFile(file);
            ownerUpdateDto.setOwnerPicture(fileName);
        }

        ownerService.updateOwner(ownerUpdateDto, ownerid);
        return ResponseEntity.status(HttpStatus.OK).body("Owner updated successfully " + ownerid);
    }

    @DeleteMapping("/{ownerid}")
    public ResponseEntity<Void> deleteOwner(@PathVariable Long ownerid) {
        logger.info("Deleting owner with ID: {}", ownerid);
        Long authenticatedOwnerId = getAuthenticatedOwnerId();
        if (!authenticatedOwnerId.equals(ownerid)) {
            throw new UnauthorizedAccessException("You are not authorized to delete this owner.");
        }
        ownerService.deleteOwner(ownerid);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{ownerid}/pets")
    public ResponseEntity<Pet> createPet(@RequestBody PetDto newPetDto) {
        logger.info("Creating pet");
        Long ownerId = getAuthenticatedOwnerId();
        Owner owner = ownerService.getOwner(ownerId);

        if (owner == null) {
            logger.error("Owner not found with ID: {}", ownerId);
            throw new OwnerNotFoundException("Owner not found with ID: " + ownerId);
        }

        Pet newPet = convertPetToEntity(newPetDto, owner);
        Pet pet = petService.createPet(newPet);

        logger.info("Pet created with ID: {}", pet.getPetId());

        return ResponseEntity.ok(pet);
    }

    @GetMapping("/{ownerid}/pets")
    public CollectionModel<EntityModel<Pet>> getPets(@PathVariable Long ownerid) {
        logger.info("Getting pets for owner with ID: {}", ownerid);
        List<EntityModel<Pet>> pets = ownerService.getOwnerPets(ownerid).stream()
                .map(PetModelAssembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(pets,
                linkTo(methodOn(OwnerController.class).getPets(ownerid)).withSelfRel());
    }

    @DeleteMapping("{ownerid}/pets/{petid}")
    public ResponseEntity<Void> deletePet(@PathVariable Long petid) {
        logger.info("Deleting pet with ID: {}", petid);
        Long ownerId = getAuthenticatedOwnerId();
        Pet pet = petService.getPet(petid);
        if (pet != null && pet.getOwner().getOwnerId() == ownerId) {
            for (Post post : pet.getPetPostList()) {
                post.setPet(null);
                postRepository.save(post);
            }
            petService.deletePet(petid);
        }
        return ResponseEntity.ok().build();
    }
//delete the @Valid @RequestBody PetDto petDto 
    @PutMapping("{ownerid}/pets/{petid}")
    public ResponseEntity<Pet> updatePet(@PathVariable Long petid,  @RequestBody PetDto petDto) {
        logger.info("Updating pet with ID: {}", petid);
        Long ownerId = getAuthenticatedOwnerId();
        Pet pet = petService.getPet(petid);
        
        if (pet != null) {
            if (pet.getOwner().getOwnerId() == ownerId) {
                pet.setPetName(petDto.getPetName());
                petService.updatePet(pet);
                return ResponseEntity.ok(pet);
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{ownerid}/follow")
    public ResponseEntity<String> followOwner(@PathVariable Long ownerid) {
        logger.info("Following owner with ID: {}", ownerid);
        Long followerid = getAuthenticatedOwnerId();

        if (followerid.equals(ownerid)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("You cannot follow yourself.");
        }

        if (ownerService.isFollowingOwner(followerid, ownerid)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("You are already following this owner.");
        } else {
            ownerService.followOwner(followerid, ownerid);
            return ResponseEntity.ok("You are now following this owner." + ownerid);
        }
    }

    @DeleteMapping("/{ownerid}/follow")
    public ResponseEntity<?> unfollowOwner(@PathVariable Long ownerid) {
        logger.info("Unfollowing owner with ID: {}", ownerid);
        Long followerid = getAuthenticatedOwnerId();

        if (ownerService.isFollowingOwner(followerid, ownerid)) {
            ownerService.unfollowOwner(followerid, ownerid);
            return ResponseEntity.ok("You have unfollowed this owner." + ownerid);
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("You are not following this owner.");
        }
    }

    @GetMapping("/{ownerid}/followers")
    public CollectionModel<EntityModel<Owner>> getFollowers(@PathVariable Long ownerid) {
        logger.info("Getting followers for owner with ID: {}", ownerid);
        List<EntityModel<Owner>> followers = ownerService.getFollowers(ownerid).stream()
                .map(ownerModelAssembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(followers,
                linkTo(methodOn(OwnerController.class).getFollowers(ownerid)).withSelfRel());
    }

    @GetMapping("/{ownerid}/following")
    public CollectionModel<EntityModel<Owner>> getFollowing(@PathVariable Long ownerid) {
        logger.info("Getting following for owner with ID: {}", ownerid);
        List<EntityModel<Owner>> following = ownerService.getFollowing(ownerid).stream()
                .map(ownerModelAssembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(following,
                linkTo(methodOn(OwnerController.class).getFollowing(ownerid)).withSelfRel());
    }

    private Pet convertPetToEntity(PetDto petDto, Owner owner) {
        Pet pet = new Pet();
        pet.setPetName(petDto.getPetName());
        pet.setOwner(owner);
        return pet;
    }

    @PostMapping("/{ownerid}/posts")
    public ResponseEntity<?> createPost(@RequestPart("post") PostDTO postDTO,
            @RequestPart("file") MultipartFile file) {
        try {
            Long authenticatedOwnerId = getAuthenticatedOwnerId();

            // Check if the pet belongs to the owner
            Long petId = postDTO.getPetId();
            if (petId != null && !petService.isPetOwnedByOwner(petId, authenticatedOwnerId)) {
                throw new AccessDeniedException(
                        "Pet with ID: " + petId + " does not belong to owner with ID: " + authenticatedOwnerId);
            }

            Owner owner = ownerService.getOwner(authenticatedOwnerId);
            if (owner == null) {
                throw new OwnerNotFoundException("Owner not found with ID: " + authenticatedOwnerId);
            }

            // Check if the owner follows the community
            Long communityId = postDTO.getCommunityId();
            if (communityId != null && !ownerService.isOwnerMemberOfCommunity(authenticatedOwnerId, communityId)) {
                throw new AccessDeniedException(
                        "Owner with ID: " + authenticatedOwnerId + " is not a member of community with ID: " + communityId);
            }

            // Handle file upload here
            if (file != null) {
                // Save the file
                String fileName = fileUploadService.saveFile(file);
                postDTO.setPostMedia(fileName);
            }

            // Create the post
            Post post = postService.createPost(postDTO, authenticatedOwnerId);

            return ResponseEntity.ok(post);
        } catch (OwnerNotFoundException e) {
            logger.error("Owner not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new CustomErrorResponse(LocalDateTime.now(), HttpStatus.NOT_FOUND.value(), e.getMessage()));
        } catch (AccessDeniedException e) {
            logger.error("Access denied: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new CustomErrorResponse(LocalDateTime.now(), HttpStatus.FORBIDDEN.value(), e.getMessage()));
        } catch (Exception e) {
            logger.error("An error occurred while creating the post: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomErrorResponse(LocalDateTime.now(), HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            e.getMessage()));
        }
    }

    @GetMapping("/{ownerid}/posts")
    public CollectionModel<EntityModel<Post>> getPosts(@PathVariable Long ownerid) {
        logger.info("Getting posts for owner with ID: {}", ownerid);
        List<EntityModel<Post>> posts = ownerService.getOwnerPosts(ownerid).stream()
                .map(postModelAssembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(posts,
                linkTo(methodOn(OwnerController.class).getPosts(ownerid)).withSelfRel());
    }
}