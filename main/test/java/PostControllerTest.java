import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.stream.Collectors;

import com.petbuddyz.petbuddyzapp.Like.LikeModelAssembler;
import com.petbuddyz.petbuddyzapp.Post.Post;
import com.petbuddyz.petbuddyzapp.Post.PostController;
import com.petbuddyz.petbuddyzapp.Post.PostModelAssembler;
import com.petbuddyz.petbuddyzapp.Post.PostService;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@SpringBootTest(classes = { PostController.class, PostModelAssembler.class,PostService.class })
@AutoConfigureMockMvc
public class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostService postService;

    @MockBean
    private PostModelAssembler postModelAssembler;

    @MockBean
    private LikeModelAssembler likeModelAssembler;
    
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(new PostController(postService, postModelAssembler, likeModelAssembler)).build();
    }

    @Test
    public void testGetPost() throws Exception {
        // Mock service behavior
        Post post = new Post();
        post.setPostId(1L);
        when(postService.getPost(1L)).thenReturn(post);

        // Perform GET request and verify the response
        mockMvc.perform(get("/posts/1")).andExpect(status().isOk());
    }
// need to Backe
    @Test
    public void testUpdatePost() throws Exception {
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
    
        // // Mock service behavior
        // Post updatedPost = new Post();
        // updatedPost.setPostId(1L);
        // when(postService.updatePost(anyLong(), anyString(), anyLong())).thenReturn(updatedPost);
    
        // mockMvc.perform(MockMvcRequestBuilders.put("/posts/1")
        //         .contentType(MediaType.APPLICATION_JSON)
        //         .content("{\"postContent\": \"Updated content\"}")
        //         .with(SecurityMockMvcRequestPostProcessors.user(userDetails))) // Authenticate the user
        //         .andExpect(status().isOk());
    }

    @Test
    public void testDeletePost() throws Exception {
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
        mockMvc.perform(delete("/posts/1").contentType(MediaType.APPLICATION_JSON)
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
                        .user(userDetails))) // Authenticate the user
                .andExpect(status().isNoContent());
    }

   
 

    // Write tests for other controller methods similarly

}
