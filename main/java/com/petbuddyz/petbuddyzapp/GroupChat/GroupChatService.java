package com.petbuddyz.petbuddyzapp.GroupChat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Service;

import com.petbuddyz.petbuddyzapp.Group.Group;
import com.petbuddyz.petbuddyzapp.Group.GroupNotFoundException;
import com.petbuddyz.petbuddyzapp.Group.GroupRepository;
import com.petbuddyz.petbuddyzapp.Message.Message;
import com.petbuddyz.petbuddyzapp.Message.MessageDto;
import com.petbuddyz.petbuddyzapp.Message.MessageNotFoundException;
import com.petbuddyz.petbuddyzapp.Message.MessageService;
import com.petbuddyz.petbuddyzapp.Notification.NotificationService;
import com.petbuddyz.petbuddyzapp.Owner.Owner;
import com.petbuddyz.petbuddyzapp.Owner.OwnerNotFoundException;
import com.petbuddyz.petbuddyzapp.Owner.OwnerRepository;
import com.petbuddyz.petbuddyzapp.Post.PostRepository;

import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class GroupChatService {

    private static final Logger logger = LoggerFactory.getLogger(GroupChatService.class);

    private final GroupChatRepository repository;
    private final GroupChatModelAssembler assembler;
    private final PostRepository postRepository;
    private final OwnerRepository ownerRepository;
    private final GroupRepository groupRepository;
    private final MessageService messageService;
    private final NotificationService notificationService;


    
    private final Message message;
    @Autowired
    public GroupChatService(GroupChatRepository repository, GroupChatModelAssembler assembler,
                            PostRepository postRepository, OwnerRepository ownerRepository,
                            GroupRepository groupRepository, MessageService messageService,
                            NotificationService notificationService,Message message) {
        this.repository = repository;
        this.assembler = assembler;
        this.postRepository = postRepository;
        this.ownerRepository = ownerRepository;
        this.groupRepository = groupRepository;
        this.messageService = messageService;
        this.notificationService = notificationService;
        this.message=message;
    }



    public GroupChat createGroupChat(Long groupId) {
        logger.info("Creating a GroupChat");
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException("Group not found with id: " + groupId));
        GroupChat chat = group.getGroupChat();
        if (chat == null) {
            chat = new GroupChat();
            group.setGroupChat(chat);
            groupRepository.save(group);
        }
        return chat;
    }




   public CollectionModel<EntityModel<GroupChat>> getAllGroupChats() {
    logger.info("Retrieving all group chats");
    List<EntityModel<GroupChat>> chats = repository.findAll().stream()
            .map(assembler::toModel)
            .collect(Collectors.toList());
    logger.info("Retrieved {} group chats", chats.size());
    return CollectionModel.of(chats);
   }




    public EntityModel<GroupChat> getGroupChatById(Long groupId) {
        logger.info("Retrieving group chat with id {}", groupId);
        GroupChat chat = repository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("GroupChat not found with id: " + groupId));
        logger.info("Retrieved group chat with id {}", groupId);
        return assembler.toModel(chat);
    }



    public GroupChat sendGroupMessage(Long groupId, Long groupChatId, MessageDto messageDto) {
        logger.info("Sending group message to group with id {}", groupId);

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException("Group not found with id: " + groupId));

        GroupChat groupChat = repository.findById(groupChatId)
                .orElseThrow(() -> new GroupChatNotFoundException("GroupChat not found with id: " + groupChatId));

        Message message = messageService.createMessage(messageDto);
        message.setGroupChat(groupChat);
        messageService.save(message);

        logger.info("Group message sent successfully to group with id {}", groupId);
        return groupChat;
    }


    public boolean isSenderAllowedToSendMessage(Long senderId, Long groupChatId,Long groupId) {
        // Retrieve the group chat from the database
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException("Group not found with id: " + groupId));
        
        // Retrieve the group associated with the group chat
        GroupChat groupChat = group.getGroupChat();
    
        // Check if the sender is a member of the group
        Set<Owner> groupMembers = group.getMembers();
        return groupMembers.stream().anyMatch(member -> member.getOwnerId().equals(senderId));
    }
    
    
    
    public void deleteGroupChat(Long groupchatId, Long groupId) {
        logger.info("Deleting group chat with id {} from group with id {}", groupchatId, groupId);



        GroupChat groupChat = repository.findById(groupchatId)
                .orElseThrow(() -> new GroupChatNotFoundException("Chat not found with id: " + groupchatId));


        repository.delete(groupChat);
        logger.info("Group message with id {} deleted successfully from group with id {}", groupchatId, groupId);
    }







public void deleteMessageFromGroupChat(Long groupChatId, Long messageId) {
        logger.info("Deleting message with ID {} from group chat with ID {}", messageId, groupChatId);

        // Check if groupChatId and messageId are valid (you can add more validation if needed)

        // Find the group chat by ID
        GroupChat groupChat = repository.findById(groupChatId)
                .orElseThrow(() -> new GroupChatNotFoundException("Group chat not found with ID: " + groupChatId));

        // Find the message by ID and delete it
       Set< Message> messages = groupChat.getMessages();
       if (messages != null) {
        Optional<Message> messageOptional = messages.stream()
                .filter(msg -> msg.getMessageId().equals(messageId))
                .findFirst();
    
        if (messageOptional.isPresent()) {
            // Delete the message
            Message messageToDelete = messageOptional.get();
            messageService.deleteById(messageToDelete.getMessageId());
            logger.info("Message with ID {} deleted successfully from group chat with ID {}", messageId, groupChatId);
        } else {
            logger.error("Message with ID {} not found in group chat with ID {}", messageId, groupChatId);
            throw new MessageNotFoundException("Message not found with ID: " + messageId);
        }
    } else {
        logger.error("No messages found in group chat with ID {}", groupChatId);
        throw new RuntimeException("No messages found in group chat");
    }




}
}