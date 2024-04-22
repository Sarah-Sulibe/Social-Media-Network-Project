import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.net.URI;
import java.util.Collections;
import java.util.Date;

import com.petbuddyz.petbuddyzapp.FileUpload.FileUploadService;
import com.petbuddyz.petbuddyzapp.GroupChat.GroupChat;
import com.petbuddyz.petbuddyzapp.GroupChat.GroupChatController;
import com.petbuddyz.petbuddyzapp.GroupChat.GroupChatModelAssembler;
import com.petbuddyz.petbuddyzapp.GroupChat.GroupChatService;
import com.petbuddyz.petbuddyzapp.Message.MessageDto;
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
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@SpringBootTest(classes = {GroupChatController.class, GroupChatService.class, GroupChatModelAssembler.class})
@AutoConfigureMockMvc
public class GroupChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GroupChatService groupchatService;

    @MockBean
    private GroupChatModelAssembler groupchatModelAssembler;
    @MockBean
    private FileUploadService fileUploadService; 
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(new GroupChatController(groupchatService, groupchatModelAssembler)).build();
    }






   

    @Test
    public void testGetGroupChatById() throws Exception {
        // Mocking group chat data
        GroupChat groupChat = new GroupChat();
        groupChat.setGroupChatId(1L);
        groupChat.setChatCreationDate(new Date());
        EntityModel<GroupChat> groupChatEntityModel = EntityModel.of(groupChat);

        // Mocking the behavior of groupChatService.getGroupChatById()
        when(groupchatService.getGroupChatById(Mockito.anyLong())).thenReturn(groupChatEntityModel);

        // Sending GET request to retrieve a specific group chat by ID
        mockMvc.perform(get("/groupChats/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.groupChatId").value(1));
    }



    @Test
    public void testCreateChat() throws Exception {
        // Mocking the group chat creation
        GroupChat createdChat = new GroupChat();
        createdChat.setGroupChatId(1L);
        createdChat.setChatCreationDate(new Date());

        // Mocking the behavior of groupchatService.createGroupChat()
        when(groupchatService.createGroupChat(Mockito.anyLong())).thenReturn(createdChat);

        // Sending POST request to create a chat
        mockMvc.perform(post("/groupChats/create/123")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.groupChatId").value(1))
                .andExpect(header().string("Location", "http://localhost/groupChats/1"));
    }
    @Test
    public void testSendGroupMessage() throws Exception {
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
     
      // Mocking the request parameters
      Long groupId = 123L;
      Long groupChatId = 456L;
      MessageDto messageDto = new MessageDto();
      messageDto.setContent("Test message");  // Set the message content explicitly
      MockMultipartFile file = new MockMultipartFile("file", "test.txt", MediaType.TEXT_PLAIN_VALUE,
                                                      "Hello, World!".getBytes());
     
     // Mocking the authenticated owner ID
  when(groupchatService.isSenderAllowedToSendMessage(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyLong()))
  .thenReturn(true);
      // Mocking the saved group chat
      GroupChat savedGroupChat = new GroupChat();
      savedGroupChat.setGroupChatId(groupChatId);
      savedGroupChat.setChatCreationDate(new Date());
      EntityModel<GroupChat> entityModel = EntityModel.of(savedGroupChat);
     
      // Mocking the behavior of the service method
      when(groupchatService.sendGroupMessage(groupId, groupChatId, messageDto)).thenReturn(savedGroupChat);
      when(groupchatModelAssembler.toModel(savedGroupChat)).thenReturn(entityModel);
     // Sending the request and verifying the response
  MockHttpServletRequestBuilder requestBuilder = (((MockMultipartHttpServletRequestBuilder) multipart("/groupChats/{groupId}/send", groupId)
  .param("groupChatId", groupChatId.toString())
  .contentType(MediaType.APPLICATION_JSON)  // Change to application/json if expected
  .content("{\"content\":\"Test message\"}")  // Set content in request body
)
  .file(file));

// mockMvc.perform(requestBuilder)
//   .andExpect(status().isOk())
//   .andExpect(jsonPath("$.groupChatId").value(groupChatId));}
    
    }


    @Test
public void testDeleteGroupChat() throws Exception {
    Long groupChatId = 123L;
    Long groupId = 456L;

    // Perform DELETE request and verify the response
    mockMvc.perform(delete("/groupChats/{groupChatId}", groupChatId)
            .param("groupId", groupId.toString())
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());
    // Verify that the service method was called with the correct arguments
    verify(groupchatService).deleteGroupChat(groupChatId, groupId);
}

@Test
public void testDeleteMessageFromGroupChat() throws Exception {
    Long groupChatId = 123L;
    Long messageId = 456L;

    // Perform DELETE request and verify the response
    mockMvc.perform(delete("/groupChats/{groupChatId}/messages/{messageId}", groupChatId, messageId)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());
    // Verify that the service method was called with the correct arguments
    verify(groupchatService).deleteMessageFromGroupChat(groupChatId, messageId);
}

}    
