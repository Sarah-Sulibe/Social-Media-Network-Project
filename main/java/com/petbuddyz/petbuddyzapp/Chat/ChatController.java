package com.petbuddyz.petbuddyzapp.Chat;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.petbuddyz.petbuddyzapp.FileUpload.FileUploadService;
import com.petbuddyz.petbuddyzapp.Message.Message;
import com.petbuddyz.petbuddyzapp.Message.MessageDto;
import com.petbuddyz.petbuddyzapp.Message.MessageModelAssembler;
import com.petbuddyz.petbuddyzapp.Pet.Pet;
import com.petbuddyz.petbuddyzapp.Security.services.UserDetailsImpl;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/chats")
public class ChatController {

    @Autowired
    private final ChatService chatService;
    @Autowired
    private final ChatModelAssembler chatModelAssembler;
    @Autowired
    private MessageModelAssembler messageModelAssembler;

    @Autowired
    private FileUploadService fileUploadService;

    public ChatController(ChatService chatService, ChatModelAssembler chatModelAssembler) {
        this.chatService = chatService;
        this.chatModelAssembler = chatModelAssembler;
    }

    private Long getAuthenticatedOwnerId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userDetails.getId();
    }

    @GetMapping
    public CollectionModel<EntityModel<Chat>> all() throws Exception {
        Collection<EntityModel<Chat>> chats = chatService.all().getContent();
        return CollectionModel.of(chats, linkTo(methodOn(ChatController.class).all()).withSelfRel());

    }

    // Single item
    @GetMapping("/{chatid}")
    public EntityModel<Chat> one(@PathVariable Long chatid) throws Exception {
        Chat chat = chatService.one(chatid);
        return chatModelAssembler.toModel(chat);
    }

    @PostMapping("/create/{receiver_id}")
    public ResponseEntity<Chat> createChat(@PathVariable Long receiver_id) throws Exception {
        Long sender_id = getAuthenticatedOwnerId();

        // Check if sender_id is the same as receiver_id
        if (sender_id.equals(receiver_id)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        // Check if a chat already exists between the sender and receiver
        if (chatService.chatExists(sender_id, receiver_id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        Long chatId = chatService.createChat(sender_id, receiver_id);

        URI locationUri = linkTo(methodOn(ChatController.class).one(chatId)).toUri();

        return ResponseEntity.created(locationUri).build();
    }

    @PostMapping("/{chatid}/send")
    public ResponseEntity<Chat> sendMessage(@RequestPart("message") MessageDto messageDto,
            @RequestPart(value = "file", required = false) MultipartFile file, @PathVariable Long chatid)
            throws Exception {
        // Check if the chat exists
        if (!chatService.exists(chatid)) {
            throw new ChatNotFoundException("Chat not found with ID: " + chatid);
        }

        // Handle file upload here
        if (file != null && !file.isEmpty()) {
            // Save the file
            String fileName = fileUploadService.saveFile(file);
            messageDto.setMedia(fileName);
        }

        chatService.sendMessage(chatid, messageDto);

        URI locationUri = linkTo(methodOn(ChatController.class).one(chatid)).toUri();

        return ResponseEntity.created(locationUri).build();
    }

    @GetMapping("/{chatid}/messages")
    public CollectionModel<EntityModel<Message>> getMessages(@PathVariable Long chatid) throws Exception {
        // Check if the chat exists
        if (!chatService.exists(chatid)) {
            throw new ChatNotFoundException("Chat not found with ID: " + chatid);
        }

        List<EntityModel<Message>> messages = chatService.getMessages(chatid).stream().map(messageModelAssembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(messages, linkTo(methodOn(ChatController.class).getMessages(chatid)).withSelfRel());
    }

}
