package com.petbuddyz.petbuddyzapp.GroupChat;
import java.net.URI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.petbuddyz.petbuddyzapp.FileUpload.FileUploadService;
import com.petbuddyz.petbuddyzapp.Message.MessageDto;
import com.petbuddyz.petbuddyzapp.Security.services.UserDetailsImpl;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@RestController
@RequestMapping("/groupChats")
public class GroupChatController {
    
private final GroupChatService groupchatService;

    private final GroupChatModelAssembler groupchatModelAssembler;


    @Autowired
    private FileUploadService fileUploadService;
    
    @Autowired
    public GroupChatController(GroupChatService groupchatService, GroupChatModelAssembler groupchatModelAssembler) {
        this.groupchatService = groupchatService;
        this.groupchatModelAssembler = groupchatModelAssembler;
    }


 


      private Long getAuthenticatedOwnerId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userDetails.getId();
    }




    @GetMapping
public ResponseEntity<CollectionModel<EntityModel<GroupChat>>> getAllGroupChats() {
    CollectionModel<EntityModel<GroupChat>> groupChats = groupchatService.getAllGroupChats();
    return ResponseEntity.ok(groupChats);
}



   

    @GetMapping("/{groupChatId}")
    public ResponseEntity<EntityModel<GroupChat>> getGroupChatById(@PathVariable Long groupChatId) {
        EntityModel<GroupChat> groupChat = groupchatService.getGroupChatById(groupChatId);
        return ResponseEntity.ok(groupChat);
    }
   


    @PostMapping("/create/{groupId}")
    public ResponseEntity<EntityModel<GroupChat>> createChat(@PathVariable Long groupId) {
        GroupChat chat = groupchatService.createGroupChat(groupId);
        URI locationUri = linkTo(methodOn(GroupChatController.class).getGroupChatById(chat.getGroupChatId())).toUri();
        EntityModel<GroupChat> chatEntityModel = EntityModel.of(chat, linkTo(methodOn(GroupChatController.class).getGroupChatById(chat.getGroupChatId())).withSelfRel());
        return ResponseEntity.created(locationUri).body(chatEntityModel);
    }
    






// //see this
//      @PostMapping("/{groupId}/send")
//     public ResponseEntity<EntityModel<GroupChat>> sendGroupMessage(
//             @PathVariable Long groupId,
//             @RequestParam Long groupChatId,
//             @RequestPart("message") MessageDto messageDto,
//              @RequestPart(value = "file", required = false) MultipartFile file) {


//                 Long sender_id = getAuthenticatedOwnerId();


                
//         // Validate that the owner signed in is the sender_id
//         if (sender_id != groupchatService.getSender(groupChatId)) {
//             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//         }


//           // Handle file upload here
//           if (file != null && !file.isEmpty()) {
//             // Save the file
//             String fileName = fileUploadService.saveFile(file);
//             messageDto.setMedia(fileName);
//         }


//         GroupChat savedGroupChat = groupchatService.sendGroupMessage(groupId, groupChatId, messageDto);
//         EntityModel<GroupChat> entityModel = groupchatModelAssembler.toModel(savedGroupChat);
//         return ResponseEntity.ok(entityModel);
//     }




@PostMapping("/{groupId}/send")
public ResponseEntity<EntityModel<GroupChat>> sendGroupMessage(
        @PathVariable Long groupId,
        @RequestParam Long groupChatId,
        @RequestPart("message") MessageDto messageDto,
        @RequestPart(value = "file", required = false) MultipartFile file) {

    Long senderId = getAuthenticatedOwnerId();

    // Validate if the authenticated owner is allowed to send the message
    if (!groupchatService.isSenderAllowedToSendMessage(senderId, groupChatId,groupId)) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    // Handle file upload here if needed
    if (file != null && !file.isEmpty()) {
        // Save the file
        String fileName = fileUploadService.saveFile(file);
        messageDto.setMedia(fileName);
    }

    // Call the sendGroupMessage method from the service class
    GroupChat savedGroupChat = groupchatService.sendGroupMessage(groupId, groupChatId, messageDto);

    // Wrap the saved GroupChat in an EntityModel
    EntityModel<GroupChat> entityModel = groupchatModelAssembler.toModel(savedGroupChat);

    // Return ResponseEntity with the saved GroupChat EntityModel
    return ResponseEntity.ok(entityModel);
}




    @DeleteMapping("/{groupChatId}")
    public ResponseEntity<EntityModel<Void>> deleteGroupChat(@PathVariable Long groupChatId, @RequestParam Long groupId) {
        groupchatService.deleteGroupChat(groupChatId, groupId);
       // Return ResponseEntity with no content and appropriate links
       return ResponseEntity.noContent().build();
    }


    @DeleteMapping("/{groupChatId}/messages/{messageId}")
    public ResponseEntity<EntityModel<Void>> deleteMessageFromGroupChat(
            @PathVariable Long groupChatId,
            @PathVariable Long messageId) {
        groupchatService.deleteMessageFromGroupChat(groupChatId, messageId);
        return ResponseEntity.noContent().build();
    }




}