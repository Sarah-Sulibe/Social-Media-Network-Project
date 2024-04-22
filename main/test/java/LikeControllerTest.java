import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import com.petbuddyz.petbuddyzapp.Like.Like;
import com.petbuddyz.petbuddyzapp.Like.LikeController;
import com.petbuddyz.petbuddyzapp.Like.LikeModelAssembler;
import com.petbuddyz.petbuddyzapp.Like.LikeService;
import com.petbuddyz.petbuddyzapp.Security.services.UserDetailsImpl;

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
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@SpringBootTest(classes = {LikeController.class, LikeService.class, LikeModelAssembler.class})
@AutoConfigureMockMvc
public class LikeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LikeService likeService;

    @MockBean
    private LikeModelAssembler likeModelAssembler;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(new LikeController(likeService, likeModelAssembler)).build();
    }

  
    @Test
    public void testGetLike() throws Exception {
        // Mock service behavior
        Like like = new Like();
        like.setLikeId(1L);
        when(likeService.getLikeById(1L)).thenReturn(EntityModel.of(like));

        // Perform GET request and verify the response
        mockMvc.perform(get("/1"))
               .andExpect(status().isOk());
    }

    // Write tests for other controller methods similarly



    @Test
    public void testLikePost() throws Exception {
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
        when(likeService.hasLikedPost(anyLong(), anyLong())).thenReturn(false); // Assuming user hasn't liked the post
        
        // Mock the behavior of adding a like
        Like savedLike = new Like(); // Assuming you have a Like class
        when(likeService.addLikeOnPost(anyLong(), anyLong())).thenReturn(savedLike);
        
        mockMvc.perform(MockMvcRequestBuilders.post("/posts/{postid}/like", 1L)
               .contentType(MediaType.APPLICATION_JSON)
               .with(SecurityMockMvcRequestPostProcessors.user(userDetails))) // Authenticate the user
               .andExpect(MockMvcResultMatchers.status().isOk()); // Assuming successful like returns status 200
    }


    @Test
    public void testDeleteLikePost() throws Exception {
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
        when(likeService.hasLikedPost(anyLong(), anyLong())).thenReturn(true); // Assuming user has liked the post
        
        mockMvc.perform(MockMvcRequestBuilders.delete("/posts/{postid}/like", 1L)
               .contentType(MediaType.APPLICATION_JSON)
               .with(SecurityMockMvcRequestPostProcessors.user(userDetails))) // Authenticate the user
               .andExpect(MockMvcResultMatchers.status().isNoContent()); // Assuming successful deletion returns status 204
    }

    @Test
    public void testLikeComment() throws Exception {
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
        
        // Mock the getAuthenticatedOwnerId() method to return owner ID 1
        when(userDetails.getId()).thenReturn(1L);
    
        // Mock service behavior
        Like savedLike = new Like(); // Assuming you create a new Like object
        when(likeService.addLikeOnComment(any(Long.class), any(Long.class))).thenReturn(savedLike);
    
        // Mock the behavior of likeModelAssembler
        when(likeModelAssembler.toModel(savedLike)).thenReturn(EntityModel.of(savedLike)); // Assuming you properly configure EntityModel.of()
    
        // Perform POST request and verify the response
        mockMvc.perform(post("/posts/{postId}/comments/{commentId}/like", 1L, 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
    

    @Test
public void testDeleteLikeComment() throws Exception {
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
    
    // Mock the getAuthenticatedOwnerId() method to return owner ID 1
    when(userDetails.getId()).thenReturn(1L);

    // Mock service behavior
    doNothing().when(likeService).deleteLikeOnComment(any(Long.class), any(Long.class));

    // Perform DELETE request and verify the response
    mockMvc.perform(delete("/posts/{postId}/comments/{commentId}/like", 1L, 1L)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());
}

}    