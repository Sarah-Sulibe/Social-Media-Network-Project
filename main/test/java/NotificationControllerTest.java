

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import com.petbuddyz.petbuddyzapp.Notification.Notification;
import com.petbuddyz.petbuddyzapp.Notification.NotificationController;
import com.petbuddyz.petbuddyzapp.Notification.NotificationModelAssembler;
import com.petbuddyz.petbuddyzapp.Notification.NotificationService;
import com.petbuddyz.petbuddyzapp.Notification.Notification.NotificationType;
import com.petbuddyz.petbuddyzapp.Owner.Owner;
import com.petbuddyz.petbuddyzapp.Owner.OwnerRepository;
import com.petbuddyz.petbuddyzapp.Security.services.UserDetailsImpl;

@SpringBootTest(classes = {NotificationController.class, NotificationService.class, NotificationModelAssembler.class})
@AutoConfigureMockMvc

class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    @MockBean
    private NotificationModelAssembler notificationModelAssembler;

    @MockBean
    private OwnerRepository ownerRepository;

    @MockBean
    private UserDetailsImpl userDetails;
    @Test
    void testCreateNotification_Success() {
        // Mock dependencies
        NotificationService notificationService = mock(NotificationService.class);
        NotificationModelAssembler notificationModelAssembler = mock(NotificationModelAssembler.class);
        OwnerRepository ownerRepository = mock(OwnerRepository.class);
        
        // Create the controller with mocked dependencies
        NotificationController notificationController = new NotificationController(notificationService, 
                notificationModelAssembler, ownerRepository);
        
        // Mock authenticated user details
        UserDetailsImpl userDetails = mock(UserDetailsImpl.class);
        when(userDetails.getId()).thenReturn(1L);
        
        // Mock Authentication object
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        
        // Mock SecurityContext
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        
        // Mock input data
        Long ownerId = 1L;
        String content = "Test content";
        Notification.NotificationType type = Notification.NotificationType.POST_LIKE; // Adjust as needed
        
        // Configure behavior of dependencies
        Notification createdNotification = new Notification(); // Create a dummy notification
        when(notificationService.createNotification(anyLong(), anyString())).thenReturn(createdNotification);
        
        // Call the method under test
        ResponseEntity<EntityModel<Notification>> response = notificationController.createNotification(ownerId, 
                content, type);
        
        // Verify the result
    assertEquals(HttpStatus.CREATED, response.getStatusCode());


    }
  
//backe to this
    @Test
    void testDeleteNotification_NotFound() throws Exception {
    //     // Mock UserDetailsImpl object
    // UserDetailsImpl userDetails = mock(UserDetailsImpl.class);
    // // Mock Authentication object
    // Authentication authentication = mock(Authentication.class);
    // // Set up the behavior of Authentication to return the mocked UserDetailsImpl object
    // when(authentication.getPrincipal()).thenReturn(userDetails);
    // // Mock SecurityContext
    // SecurityContext securityContext = mock(SecurityContext.class);
    // SecurityContextHolder.setContext(securityContext);
    // // Set up the behavior of SecurityContext to return the mocked Authentication object
    // when(securityContext.getAuthentication()).thenReturn(authentication);

    //     when(notificationService.getnotificationById(anyLong())).thenReturn(null);

    //     mockMvc.perform(delete("/notifications/{notificationId}", 1L))
    //             .andExpect(status().isNotFound());
    }
//backe to this
    @Test
    void testDeleteAllNotifications_NotFound() throws Exception {
   
    }
    @Test
    public void testSendNotification_Unauthorized() throws Exception {
      // Clear any previous security context
      SecurityContextHolder.clearContext();
    
      // ... existing mock setup code (excluding security context) ...
    
      mockMvc.perform(post("/notifications/send")
          .param("ownerId", "1")
          .param("message", "Test Message")
          .param("type", "POST_LIKE")
          .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isForbidden());
    }
    
  
}
