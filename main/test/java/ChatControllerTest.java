
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
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
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.petbuddyz.petbuddyzapp.Chat.ChatController;
import com.petbuddyz.petbuddyzapp.Chat.ChatModelAssembler;
import com.petbuddyz.petbuddyzapp.Chat.ChatService;
import com.petbuddyz.petbuddyzapp.FileUpload.FileUploadService;
import com.petbuddyz.petbuddyzapp.Message.MessageModelAssembler;
import com.petbuddyz.petbuddyzapp.Security.services.UserDetailsImpl;
@SpringBootTest(classes = {ChatController.class, ChatService.class, ChatModelAssembler.class})
@AutoConfigureMockMvc
public class ChatControllerTest {
   


    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChatService chatService;

    @MockBean
    private ChatModelAssembler chatModelAssembler;

    @MockBean
    private MessageModelAssembler messageModelAssembler;

    @MockBean
    private FileUploadService fileUploadService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(new ChatController(chatService, chatModelAssembler)).build();
    }







    @Test
    public void testCreateChat() throws Exception {
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
        // Mocking the chatService
        when(chatService.createChat(anyLong(), anyLong())).thenReturn(1L);

        // Perform the POST request
        mockMvc.perform(post("/chats/create/2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }
    

    @Test
    public void testSendMessage_MissingMessageContent() throws Exception {
      // Mock chat existence
      when(chatService.exists(anyLong())).thenReturn(true);
    
      // Perform the POST request with no message parameter
      mockMvc.perform(multipart("/chats/1/send")
          .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isBadRequest());
      //  .andExpect(jsonPath("$.message").value("Message content is required"));
    }
    
    
   
    

}

