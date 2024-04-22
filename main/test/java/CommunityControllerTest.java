import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petbuddyz.petbuddyzapp.Community.Community;
import com.petbuddyz.petbuddyzapp.Community.CommunityController;
import com.petbuddyz.petbuddyzapp.Community.CommunityDto;
import com.petbuddyz.petbuddyzapp.Community.CommunityModelAssembler;
import com.petbuddyz.petbuddyzapp.Community.CommunityRepository;
import com.petbuddyz.petbuddyzapp.Community.CommunityService;
import com.petbuddyz.petbuddyzapp.Owner.Owner;
import com.petbuddyz.petbuddyzapp.Owner.OwnerModelAssembler;
import com.petbuddyz.petbuddyzapp.Owner.OwnerService;
import com.petbuddyz.petbuddyzapp.Post.Post;
import com.petbuddyz.petbuddyzapp.Post.PostModelAssembler;
import com.petbuddyz.petbuddyzapp.Security.services.UserDetailsImpl;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
@SpringBootTest(classes = {CommunityController.class, CommunityService.class, CommunityModelAssembler.class})
@AutoConfigureMockMvc
public class CommunityControllerTest {


    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CommunityService communityService;

    @MockBean
    private CommunityModelAssembler communityModelAssembler;


    @MockBean
    private OwnerModelAssembler ownerModelAssembler;

    @MockBean
    private OwnerService ownerService;
    @MockBean
    CommunityRepository   communityRepository ;


    @MockBean
    private PostModelAssembler postModelAssembler;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(new CommunityController(communityService, communityModelAssembler,ownerModelAssembler)).build();
    }


   
    @Test
    public void testGetAllCommunities_Success() throws Exception {
        // Mock data
        Community community1 = new Community();
        community1.setCommunityId(1L);
        community1.setCommunityName("Community 1");

        Community community2 = new Community();
        community2.setCommunityId(2L);
        community2.setCommunityName("Community 2");

        List<Community> communities = new ArrayList<>();
        communities.add(community1);
        communities.add(community2);

        when(communityService.getAllCommunities()).thenReturn(communities);

        // Perform GET request
        mockMvc.perform(MockMvcRequestBuilders.get("/communities"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));

        
    }
    @Test
public void testGetCommunity_Success() throws Exception {
    // Mock data
    Long communityId = 1L;
    Community community = new Community();
    community.setCommunityId(communityId);
    community.setCommunityName("Test Community");

    when(communityService.getCommunity(communityId)).thenReturn(community);
    when(communityModelAssembler.toModel(community)).thenReturn(EntityModel.of(community));

    // Perform GET request
    mockMvc.perform(MockMvcRequestBuilders.get("/communities/{communityId}", communityId))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));

    // Verify response
    // You may need to adjust the response verification according to your actual implementation
}

    
@Test
public void testCreateCommunity_Success() {

    // Mock dependencies
    communityService = mock(CommunityService.class);
    communityModelAssembler = mock(CommunityModelAssembler.class);
    ownerService = mock(OwnerService.class);
    CommunityController  communityController = new CommunityController(communityService, communityModelAssembler, ownerService);
    // Mock UserDetailsImpl object
    UserDetailsImpl userDetails = mock(UserDetailsImpl.class);
    // Mock Authentication object
    Authentication authentication = mock(Authentication.class);
    // Set up the behavior of Authentication to return the mocked UserDetailsImpl object
    when(authentication.getPrincipal()).thenReturn(userDetails);
    // Mock SecurityContext
    SecurityContext securityContext = mock(SecurityContext.class);
    SecurityContextHolder.setContext(securityContext);
    // Set up the behavior of SecurityContext to return the mocked Authentication object
    when(securityContext.getAuthentication()).thenReturn(authentication);

    // Mock input data
    CommunityDto communityDto = new CommunityDto();
    communityDto.setCommunityName("Test Community");
    communityDto.setCommunityDescription("Description of test community");

    // Mock behavior of dependencies
    when(communityController.getAuthenticatedOwnerId()).thenReturn(1L);
    Owner owner = new Owner();
    owner.setOwnerId(1L);
    when(ownerService.getOwner(1L)).thenReturn(owner);
    when(communityService.createCommunity(any(Community.class))).thenReturn(new Community());

    // Call the method under test
    ResponseEntity<Community> response = communityController.createCommunity(communityDto);

    // Verify the result
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
}






       
    @Test
    public void testGetCommunityMembers_Success() throws Exception {
        
        // Mock data
        Long communityId = 1L;
        List<Owner> members = new ArrayList<>();
        when(communityService.getCommunityMembers(communityId)).thenReturn(members);

        // Perform request and verify success
        mockMvc.perform(MockMvcRequestBuilders.get("/communities/{communityId}/members", communityId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                ; // Print response to console (optional)
    }

    @Test
    public void testDeleteCommunity_Success() throws Exception {
        // Mock UserDetailsImpl object
        UserDetailsImpl userDetails = Mockito.mock(UserDetailsImpl.class);
        when(userDetails.getId()).thenReturn(1L); // Set the owner ID

        // Mock Authentication object
        Authentication authentication = Mockito.mock(Authentication.class);

        // Set up the behavior of Authentication to return the mocked UserDetailsImpl object
        when(authentication.getPrincipal()).thenReturn(userDetails);

        // Mock SecurityContext
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        // Set up the behavior of SecurityContext to return the mocked Authentication object
        when(securityContext.getAuthentication()).thenReturn(authentication);

        // Mock service behavior
        Community mockCommunity = new Community();
        Owner mockOwner = new Owner();
        mockOwner.setOwnerId(1L); // Set owner ID
        mockCommunity.setCommunityOwner(mockOwner); // Set community owner
        doReturn(mockCommunity).when(communityService).getCommunity(any(Long.class));

        // Perform DELETE request and verify the response
        mockMvc.perform(delete("/communities/{communityId}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
    @Test
    public void testUpdateCommunity_Success() throws Exception {
        // Mock UserDetailsImpl object
        UserDetailsImpl userDetails = Mockito.mock(UserDetailsImpl.class);
        when(userDetails.getId()).thenReturn(1L); // Set the owner ID

        // Mock Authentication object
        Authentication authentication = Mockito.mock(Authentication.class);

        // Set up the behavior of Authentication to return the mocked UserDetailsImpl object
        when(authentication.getPrincipal()).thenReturn(userDetails);

        // Mock SecurityContext
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        // Set up the behavior of SecurityContext to return the mocked Authentication object
        when(securityContext.getAuthentication()).thenReturn(authentication);

        // Mock service behavior
        Community mockCommunity = new Community();
        Owner mockOwner = new Owner();
        mockOwner.setOwnerId(1L); // Set owner ID
        mockCommunity.setCommunityOwner(mockOwner); // Set community owner
        when(communityService.getCommunity(any(Long.class))).thenReturn(mockCommunity);

        // Mock request body
        CommunityDto communityDto = new CommunityDto();
        communityDto.setCommunityName("Updated Community Name");
        communityDto.setCommunityDescription("Updated Community Description");

        // Perform PUT request and verify the response
        mockMvc.perform(put("/communities/{communityId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(communityDto)))
                .andExpect(status().isOk());
    }
  
    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }






}


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