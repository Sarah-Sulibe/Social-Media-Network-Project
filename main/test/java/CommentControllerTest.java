

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.EntityModel;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import com.petbuddyz.petbuddyzapp.Comment.Comment;
import com.petbuddyz.petbuddyzapp.Comment.CommentController;
import com.petbuddyz.petbuddyzapp.Comment.CommentDto;
import com.petbuddyz.petbuddyzapp.Comment.CommentModelAssembler;
import com.petbuddyz.petbuddyzapp.Comment.CommentService;
import com.petbuddyz.petbuddyzapp.Security.services.UserDetailsImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.ArgumentMatchers.anyLong;
@SpringBootTest(classes = {CommentController.class, CommentService.class, CommentModelAssembler.class})
@AutoConfigureMockMvc
public class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentService commentService;

    @MockBean
    private CommentModelAssembler commentModelAssembler;

  
    

    @Test
public void testCreateComment() {
    // Mock dependencies
    CommentService commentService = mock(CommentService.class);
    CommentModelAssembler commentModelAssembler = mock(CommentModelAssembler.class);

    // Create instance of your controller with mocked dependencies
    CommentController controller = new CommentController(commentService, commentModelAssembler);

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
    Long postId = 1L;
    CommentDto commentDto = new CommentDto();
    commentDto.setContent("Test comment content");

    // Mock behavior of dependencies
    when(controller.getAuthenticatedOwnerId()).thenReturn(1L); // Assuming authenticated owner ID is 1L
    when(commentService.createPostComment(anyLong(), eq(commentDto), anyLong())).thenReturn(new Comment()); // Assuming createPostComment returns a new comment
    EntityModel<Comment> commentModel = EntityModel.of(new Comment());
    when(commentModelAssembler.toModel(any(Comment.class))).thenReturn(commentModel);

    // Call the method under test
    EntityModel<Comment> response = controller.createComment(postId, commentDto);

    // Verify the result
    assertNotNull(response);
    assertNotNull(response.getContent());
    assertEquals(commentModel.getContent(), response.getContent());
}
@Test
    public void testDeletePostComment() throws Exception {
        CommentService commentService = mock(CommentService.class);
        CommentModelAssembler commentModelAssembler = mock(CommentModelAssembler.class);
    
        // Create instance of your controller with mocked dependencies
        CommentController controller = new CommentController(commentService, commentModelAssembler);
    
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
        long postId = 1L;
        long commentId = 2L;

   
        
       
        // // Successful deletion
        // doReturn(ResponseEntity.noContent().build())
        //     .when(commentService)
        //     .deletePostComment(postId, commentId, userDetails.getId());

        // mockMvc.perform(delete("/posts/{postId}/comments/{commentId}", postId, commentId))
        //        .andExpect(status().isNoContent());

            //    doNothing()
            //    .when(commentService)
            //    .deletePostComment(postId, commentId, userDetails.getId());
           
           

        mockMvc.perform(delete("/posts/{postId}/comments/{commentId}", postId, commentId))
               .andExpect(status().isForbidden());

        // // Comment not found
        // doThrow(CommentNotFoundException.class)
        //     .when(commentService)
        //     .deletePostComment(postId, commentId, userDetails.getId());

        // mockMvc.perform(delete("/posts/{postId}/comments/{commentId}", postId, commentId))
        //        .andExpect(status().isNotFound());
    }
}
