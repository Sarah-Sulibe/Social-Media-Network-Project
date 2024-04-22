import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.petbuddyz.petbuddyzapp.Group.Group;
import com.petbuddyz.petbuddyzapp.Group.GroupController;
import com.petbuddyz.petbuddyzapp.Group.GroupModelAssembler;
import com.petbuddyz.petbuddyzapp.Group.GroupService;
import com.petbuddyz.petbuddyzapp.Security.services.UserDetailsImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
@SpringBootTest(classes = {GroupController.class, GroupService.class, GroupModelAssembler.class})
@AutoConfigureMockMvc
public class GroupControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GroupService groupService;

    @MockBean
    private GroupModelAssembler groupModelAssembler;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(new GroupController(groupService, groupModelAssembler)).build();
    }

   


    
    @Test
    public void testGetGroup() throws Exception {
        // Mock service behavior
        Group group = new Group();
        group.setGroupId(1L);
        when(groupService.one(1L)).thenReturn(EntityModel.of(group));

        // Perform GET request and verify the response
        mockMvc.perform(get("/groups/1"))
               .andExpect(status().isOk());
    }



// Add the necessary imports for Mockito and SecurityContext

@Test
public void testCreateGroup() throws Exception {
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

    // Mock service behavior
    Group group = new Group();
    group.setGroupId(1L);
    when(groupService.createGroup(any(), any())).thenReturn(group);

    // Perform POST request and verify the response
    mockMvc.perform(post("/groups/1/create")
           .param("groupMakerId", "1")
           .param("groupName", "Test Group")
           .contentType(MediaType.APPLICATION_JSON))
           .andExpect(status().isCreated());
}
@Test
public void testJoinGroup() throws Exception {
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

    // Mock service behavior
    Group group = new Group();
    group.setGroupId(1L);
    when(groupService.joinGroup(any(), any(), any())).thenReturn(group);

    // Perform POST request and verify the response
    mockMvc.perform(post("/groups/1/join")
            .param("groupName", "Test Group")
            .contentType(MediaType.APPLICATION_JSON) // Set the content type to JSON
            .content("1")) // Set the ownerId as the request body
            .andExpect(status().isCreated());
}




@Test
public void testDeleteMember() throws Exception {
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

    // Mock service behavior
    Group group = new Group();
    group.setGroupId(1L);
    when(groupService.RemoveMemberfromGroup(any(), any())).thenReturn(group);

    // Perform DELETE request and verify the response
    mockMvc.perform(delete("/groups/1/members/1"))
           .andExpect(status().isNoContent());
}

    @Test
    public void testAddMemberToGroup() throws Exception {
        // Mock service behavior
        when(groupService.AddMembertoGroup(any(), any())).thenReturn(new Group());

        // Perform PUT request and verify the response
        mockMvc.perform(put("/groups/1/members/1"))
               .andExpect(status().isOk());
    }

    @Test
    public void testDeleteGroup() throws Exception {
        // Perform DELETE request and verify the response
        mockMvc.perform(delete("/groups/1?requesterId=1"))
               .andExpect(status().isNoContent());
    }

}