import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import static org.mockito.Mockito.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.petbuddyz.petbuddyzapp.FileUpload.FileUploadService;
import com.petbuddyz.petbuddyzapp.Owner.Owner;
import com.petbuddyz.petbuddyzapp.Owner.OwnerController;
import com.petbuddyz.petbuddyzapp.Owner.OwnerModelAssembler;
import com.petbuddyz.petbuddyzapp.Owner.OwnerService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(classes = {OwnerController.class, OwnerService.class, OwnerModelAssembler.class})
@AutoConfigureMockMvc
public class OwnerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OwnerService ownerService;

    @MockBean
    private OwnerModelAssembler ownerModelAssembler;

    @MockBean
    private PetService petService;

    @MockBean
    private PetModelAssembler petModelAssembler;

    @MockBean
    private PostService postService; 

    @MockBean
    private PostModelAssembler postModelAssembler;


    
    @MockBean
    private PostRepository postRepository;

    @MockBean
    private FileUploadService fileUploadService;



    // Initialize ObjectMapper
    ObjectMapper objectMapper = new ObjectMapper();


    

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(new OwnerController(ownerService, ownerModelAssembler, petModelAssembler, petService)).build();
    }

    
    
    @Test
    public void testGetOwnerById() throws Exception {
        // Mock service behavior
        Owner owner = new Owner();
        owner.setOwnerId(1L);
        when(ownerService.getOwner(1L)).thenReturn(owner);

        // Perform GET request and verify the response
        mockMvc.perform(get("/owners/1"))
               .andExpect(status().isOk());
    }
    @Test
public void testUpdateOwner() throws Exception {
    // Mock UserDetailsImpl object
    // UserDetailsImpl userDetails = Mockito.mock(UserDetailsImpl.class);

    // // Mock Authentication object
    // Authentication authentication = Mockito.mock(Authentication.class);

    // // Set up the behavior of Authentication to return the mocked UserDetailsImpl object
    // Mockito.when(authentication.getPrincipal()).thenReturn(userDetails);

    // // Mock SecurityContext
    // SecurityContext securityContext = Mockito.mock(SecurityContext.class);
    // SecurityContextHolder.setContext(securityContext);

    // // Set up the behavior of SecurityContext to return the mocked Authentication object
    // Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);

    // // Mock service behavior
    // Owner owner = new Owner();
    // owner.setOwnerId(1L);
    // String jsonOwner = new ObjectMapper().writeValueAsString(owner);

    // Mockito.doNothing().when(ownerService).updateOwner(Mockito.any(), Mockito.any());

    // // Perform PUT request and verify the response
    // mockMvc.perform(put("/owners/1")
    //        .contentType(MediaType.APPLICATION_JSON)
    //        .content(jsonOwner))
    //        .andExpect(status().isOk())
    //        .andExpect(jsonPath("$.id").value(1));
}
//Ba
@Test
public void testDeleteOwner() throws Exception {
    // // Mock UserDetailsImpl object
    // UserDetailsImpl userDetails = Mockito.mock(UserDetailsImpl.class);
    
    // // Mock Authentication object
    // Authentication authentication = Mockito.mock(Authentication.class);
    
    // // Set up the behavior of Authentication to return the mocked UserDetailsImpl object
    // when(authentication.getPrincipal()).thenReturn(userDetails);
    
    // // Mock SecurityContext
    // SecurityContext securityContext = Mockito.mock(SecurityContext.class);
    // SecurityContextHolder.setContext(securityContext);
    
    // // Set up the behavior of SecurityContext to return the mocked Authentication object
    // when(securityContext.getAuthentication()).thenReturn(authentication);
    
    // // Mock the getAuthenticatedOwnerId() method to return owner ID 1
    

    // // Perform DELETE request and verify the response
    // mockMvc.perform(delete("/owners/1"))
    //        .andExpect(status().isOk());
}


@Test
    public void testCreatePet_Success() {
        // Mock dependencies
        OwnerService ownerService = mock(OwnerService.class);
        PetService petService = mock(PetService.class);

        // Create instance of your controller with mocked dependencies
        OwnerController controller = new OwnerController(ownerService, ownerModelAssembler, petModelAssembler, petService);
        UserDetailsImpl userDetails = Mockito.mock(UserDetailsImpl.class);
    
        // Mock Authentication object
        Authentication authentication = Mockito.mock(Authentication.class);
        
        // Set up the behavior of Authentication to return the mocked UserDetailsImpl object
        when(authentication.getPrincipal()).thenReturn(userDetails);
        
        // Mock SecurityContext
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        // Mock input data
        PetDto newPetDto = new PetDto();
        // Set necessary fields in newPetDto

        // Mock behavior of dependencies
        Long ownerId = 1L;
        Owner owner = new Owner();
        // Set necessary fields in owner
        when(controller.getAuthenticatedOwnerId()).thenReturn(ownerId);
        when(ownerService.getOwner(ownerId)).thenReturn(owner);
        when(petService.createPet(any(Pet.class))).thenReturn(new Pet());

        // Call the method under test
        ResponseEntity<Pet> response = controller.createPet(newPetDto);

        // Verify the result
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        // Additional assertions can be added to verify the returned Pet object
    }

@Test
public void testDeletePet() throws Exception {
    // Mock UserDetailsImpl object
    UserDetailsImpl userDetails = Mockito.mock(UserDetailsImpl.class);
    
    // Mock Authentication object
    Authentication authentication = Mockito.mock(Authentication.class);
    
    // Set up the behavior of Authentication to return the mocked UserDetailsImpl object
    when(authentication.getPrincipal()).thenReturn(userDetails);
    
    // Mock SecurityContext
    SecurityContext securityContext = Mockito.mock(SecurityContext.class);
    SecurityContextHolder.setContext(securityContext);
    
    // Set up the behavior of SecurityContext to return the mocked Authentication object
    when(securityContext.getAuthentication()).thenReturn(authentication);

    // Perform DELETE request and verify the response
    mockMvc.perform(delete("/owners/1/pets/1"))
           .andExpect(status().isOk());
}

@Test
void testUpdatePet() throws Exception {
    // Create an instance of OwnerController
    OwnerController ownerController = new OwnerController(ownerService, ownerModelAssembler, petModelAssembler, petService);

    // Mock UserDetailsImpl object
    UserDetailsImpl userDetails = Mockito.mock(UserDetailsImpl.class);

    // Mock Authentication object
    Authentication authentication = Mockito.mock(Authentication.class);

    // Set up the behavior of Authentication to return the mocked UserDetailsImpl object
    Mockito.when(authentication.getPrincipal()).thenReturn(userDetails);

    // Mock SecurityContext
    SecurityContext securityContext = Mockito.mock(SecurityContext.class);
    SecurityContextHolder.setContext(securityContext);

    // Set up the behavior of SecurityContext to return the mocked Authentication object
    Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);

    // Mock authenticated owner ID
    when(ownerController.getAuthenticatedOwnerId()).thenReturn(1L);

    // Mock pet data
    PetDto petDto = new PetDto();
    petDto.setPetName("UpdatedPetName");

    // Mock existing pet
    Pet existingPet = new Pet();
    existingPet.setPetId(1L);
    Owner owner = new Owner();
    owner.setOwnerId(1L);
    existingPet.setOwner(owner);

    // Mock pet service behavior
    when(petService.getPet(1L)).thenReturn(existingPet);
// Perform PUT request and verify the response
mockMvc.perform(put("/owners/1/pets/1") // Ensure this URL matches the actual endpoint mapping
        .contentType(MediaType.APPLICATION_JSON)
        .content(asJsonString(petDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.petName").value("UpdatedPetName")); // Ensure pet name is updated

}


// Utility method to convert object to JSON string
private static String asJsonString(final Object obj) {
    try {
        return new ObjectMapper().writeValueAsString(obj);
    } catch (Exception e) {
        throw new RuntimeException(e);
    }
}




    @Test
    public void testFollowOwner_SelfFollow() throws Exception {
        // Mock authenticated owner ID
         // Create an instance of OwnerController
    OwnerController ownerController = new OwnerController(ownerService, ownerModelAssembler, petModelAssembler, petService);

    // Mock UserDetailsImpl object
    UserDetailsImpl userDetails = Mockito.mock(UserDetailsImpl.class);

    // Mock Authentication object
    Authentication authentication = Mockito.mock(Authentication.class);

    // Set up the behavior of Authentication to return the mocked UserDetailsImpl object
    Mockito.when(authentication.getPrincipal()).thenReturn(userDetails);

    // Mock SecurityContext
    SecurityContext securityContext = Mockito.mock(SecurityContext.class);
    SecurityContextHolder.setContext(securityContext);

    // Set up the behavior of SecurityContext to return the mocked Authentication object
    Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);

    // Mock authenticated owner ID
    when(ownerController.getAuthenticatedOwnerId()).thenReturn(1L);
       

        // Perform POST request and verify the response
        mockMvc.perform(post("/owners/1/follow"))
               .andExpect(status().isConflict())
               .andExpect(content().string("You cannot follow yourself."));
    }

  






@Test
public void testUnfollowOwner_Success() throws Exception {
    OwnerController ownerController = new OwnerController(ownerService, ownerModelAssembler, petModelAssembler, petService);

    // Mock UserDetailsImpl object
    UserDetailsImpl userDetails = Mockito.mock(UserDetailsImpl.class);

    // Mock Authentication object
    Authentication authentication = Mockito.mock(Authentication.class);

    // Set up the behavior of Authentication to return the mocked UserDetailsImpl object
    Mockito.when(authentication.getPrincipal()).thenReturn(userDetails);

    // Mock SecurityContext
    SecurityContext securityContext = Mockito.mock(SecurityContext.class);
    SecurityContextHolder.setContext(securityContext);

    // Set up the behavior of SecurityContext to return the mocked Authentication object
    Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);

    // Mock authenticated owner ID
    when(ownerController.getAuthenticatedOwnerId()).thenReturn(1L);
    Long ownerId = 2L; // Owner to be unfollowed

    // Mock owner service behavior
    when(ownerService.isFollowingOwner(1L, ownerId)).thenReturn(true);

    // Perform DELETE request and verify the response
    mockMvc.perform(delete("/owners/2/follow"))
            .andExpect(status().isOk())
            .andExpect(content().string("You have unfollowed this owner." + ownerId));
}




@Test
public void testUnfollowOwner_NotFollowing() throws Exception {
    OwnerController ownerController = new OwnerController(ownerService, ownerModelAssembler, petModelAssembler, petService);

    // Mock UserDetailsImpl object
    UserDetailsImpl userDetails = Mockito.mock(UserDetailsImpl.class);

    // Mock Authentication object
    Authentication authentication = Mockito.mock(Authentication.class);

    // Set up the behavior of Authentication to return the mocked UserDetailsImpl object
    Mockito.when(authentication.getPrincipal()).thenReturn(userDetails);

    // Mock SecurityContext
    SecurityContext securityContext = Mockito.mock(SecurityContext.class);
    SecurityContextHolder.setContext(securityContext);

    // Set up the behavior of SecurityContext to return the mocked Authentication object
    Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
    // Mock authenticated owner ID
    when(ownerController.getAuthenticatedOwnerId()).thenReturn(1L);
    Long ownerId = 2L; // Owner to be unfollowed

    // Mock owner service behavior
    when(ownerService.isFollowingOwner(1L, ownerId)).thenReturn(false);

    // Perform DELETE request and verify the response
    mockMvc.perform(delete("/owners/2/follow"))
            .andExpect(status().isConflict())
            .andExpect(content().string("You are not following this owner."));
}




@Test
void testGetFollowers() {
    // Mock owner service behavior
    OwnerController ownerController = new OwnerController(ownerService, ownerModelAssembler, petModelAssembler, petService);
    when(ownerService.getFollowers(1L)).thenReturn(Arrays.asList(new Owner(), new Owner()));

    // Call the controller method
    CollectionModel<EntityModel<Owner>> followers = ownerController.getFollowers(1L);

    // Verify the returned CollectionModel
    assertNotNull(followers);
    assertEquals(2, followers.getContent().size());
}



@Test
void testGetFollowing() {
    // Mock owner service behavior
    OwnerController ownerController = new OwnerController(ownerService, ownerModelAssembler, petModelAssembler, petService);
    
    when(ownerService.getFollowing(1L)).thenReturn(Arrays.asList(new Owner(), new Owner()));

    // Call the controller method
    CollectionModel<EntityModel<Owner>> following = ownerController.getFollowing(1L);

    // Verify the returned CollectionModel
    assertNotNull(following);
    assertEquals(2, following.getContent().size());
}








@Test
public void testCreatePost() {
    // Mock dependencies
    OwnerService ownerService = mock(OwnerService.class);
    PetService petService = mock(PetService.class);
    FileUploadService fileUploadService = mock(FileUploadService.class);
    PostService postService = mock(PostService.class);

    // Create instance of your controller with mocked dependencies
    OwnerController controller = new OwnerController(ownerService, petService, fileUploadService, postService);
 // Mock UserDetailsImpl object
 UserDetailsImpl userDetails = Mockito.mock(UserDetailsImpl.class);

 // Mock Authentication object
 Authentication authentication = Mockito.mock(Authentication.class);

 // Set up the behavior of Authentication to return the mocked UserDetailsImpl object
 Mockito.when(authentication.getPrincipal()).thenReturn(userDetails);

 // Mock SecurityContext
 SecurityContext securityContext = Mockito.mock(SecurityContext.class);
 SecurityContextHolder.setContext(securityContext);

 // Set up the behavior of SecurityContext to return the mocked Authentication object
 Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
    // Mock input data
    PostDTO postDTO = new PostDTO();
    postDTO.setPetId(1L);
    postDTO.setCommunityId(2L);

    MultipartFile file = mock(MultipartFile.class);

      // Mock behavior of dependencies
      when(controller.getAuthenticatedOwnerId()).thenReturn(1L);
      when(petService.isPetOwnedByOwner(1L, 1L)).thenReturn(true);
      when(ownerService.getOwner(1L)).thenReturn(new Owner());
      when(ownerService.isOwnerMemberOfCommunity(1L, 2L)).thenReturn(true);
      when(fileUploadService.saveFile(file)).thenReturn("mockedFileName");
      when(postService.createPost(postDTO, 1L)).thenReturn(new Post());

      // Call the method under test
      ResponseEntity<?> response = controller.createPost(postDTO, file);

      // Verify the result
      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertNotNull(response.getBody());
      assertTrue(response.getBody() instanceof Post);
  
}




@Test
    public void testGetPosts_Success() {
        // Mock dependencies
        OwnerService ownerService = mock(OwnerService.class);
        PostModelAssembler postModelAssembler = mock(PostModelAssembler.class);

        // Create instance of your controller with mocked dependencies
        OwnerController controller = new OwnerController(ownerService, postModelAssembler);

        // Mock input data
        Long ownerId = 1L;

        // Mock behavior of dependencies
        List<Post> postsList = new ArrayList<>();
        postsList.add(new Post());
        postsList.add(new Post());
        when(ownerService.getOwnerPosts(ownerId)).thenReturn(postsList);

        // Mock behavior of postModelAssembler
        when(postModelAssembler.toModel(any(Post.class))).thenAnswer(invocation -> {
            Post post = invocation.getArgument(0);
            return EntityModel.of(post,
                    linkTo(methodOn(OwnerController.class).getPosts(ownerId)).withSelfRel());
        });

        // Call the method under test
        CollectionModel<EntityModel<Post>> result = controller.getPosts(ownerId);

        // Verify the result
        assertNotNull(result);
        assertFalse(result.getContent().isEmpty());
        assertEquals(postsList.size(), result.getContent().size());
    }
}