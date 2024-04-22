package com.petbuddyz.petbuddyzapp.Chat;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;

import org.springframework.stereotype.Service;
import com.petbuddyz.petbuddyzapp.Message.Message;
import com.petbuddyz.petbuddyzapp.Message.MessageDto;
import com.petbuddyz.petbuddyzapp.Message.MessageService;
import com.petbuddyz.petbuddyzapp.Owner.OwnerNotFoundException;
import com.petbuddyz.petbuddyzapp.Owner.OwnerRepository;

@Service
public class ChatService {

    private static final Logger logger = LoggerFactory.getLogger(ChatService.class);

    @Autowired
    private ChatRepository repository;
    @Autowired
    private OwnerRepository ownerRepository;
    @Autowired
    private ChatModelAssembler assembler;
    @Autowired
    private Message message;
    @Autowired
    private MessageService messageService;

    // get all chats
    public CollectionModel<EntityModel<Chat>> all() throws Exception {

        logger.info("Getting Chats ");
        try {

            List<EntityModel<Chat>> chats = repository.findAll().stream() //
                    .map(assembler::toModel) //
                    .collect(Collectors.toList());

            return CollectionModel.of(chats, linkTo(methodOn(ChatController.class).all()).withSelfRel());
        } catch (Exception ex) {
            // TODO: handle exception
            logger.error("Error while getting chats: {}", ex.getMessage());
            throw ex;

        }
    }

    // get a chat
    public Chat one(Long chatId) throws Exception {
        logger.info("Getting Chat by ID");
        try {

            Chat chat = repository.findById(chatId)
                    .orElseThrow(() -> new ChatNotFoundException("Chat not found with id: " + chatId));
            return chat;
        } catch (Exception ex) {

            logger.error("Error while getting chat by ID: {}", ex.getMessage());
            throw ex;
        }

    }

    // send a message
    public Long sendMessage(Long chatId, MessageDto messageDto) throws Exception {
        if (chatId == null) {
            throw new IllegalArgumentException("Chat ID must not be null");
        }

        logger.info("Sending a message");
        try {
            Message message = messageService.createMessage(messageDto);
            Chat chat = repository.findById(chatId)
                    .orElseThrow(() -> new ChatNotFoundException("Chat not found with id: " + chatId));
            message.setChat(chat);
            chat.getMessages().add(message);
            repository.save(chat);
            return message.getMessageId();
        } catch (Exception ex) {
            logger.error("Error while sending a message: {}", ex.getMessage());
            throw ex;
        }
    }

    public void deleteMessage(Long messageId) {
        logger.info("Find the chat object by its ID");
        Chat chat = repository.findById(messageId)
                .orElseThrow(() -> new ChatNotFoundException("Chat not found with id: " + messageId));
        logger.info("Delete the chat object");
        repository.delete(chat);
    }

    public Long createChat(Long sender_id, Long receiver_id) {
        logger.info("Creating a chat");
        Chat chat = new Chat();
        chat.setSender(ownerRepository.findById(sender_id)
                .orElseThrow(() -> new OwnerNotFoundException("Owner not found with id: " + sender_id)));
        chat.setReceiver(ownerRepository.findById(receiver_id)
                .orElseThrow(() -> new OwnerNotFoundException("Owner not found with id: " + receiver_id)));
        message.setStatus(Message.Status.UNREAD);
        Chat savedChat = repository.save(chat);
        return savedChat.getChatId();
    }

    public Long getSender(Long chatId) {
        logger.info("Getting the sender of the chat");
        Chat chat = repository.findById(chatId)
                .orElseThrow(() -> new ChatNotFoundException("Chat not found with id: " + chatId));
        return chat.getSender().getOwnerId();
    }

    public boolean exists(Long chatid) {
        logger.info("Checking if the chat exists");
        return repository.existsById(chatid);
    }

    public Set<Message> getMessages(Long chatid) {
        logger.info("Getting messages of the chat");
        Chat chat = repository.findById(chatid)
                .orElseThrow(() -> new ChatNotFoundException("Chat not found with id: " + chatid));
        return chat.getMessages();
    }

    public boolean chatExists(Long sender_id, Long receiver_id) {
        logger.info("Checking if the chat exists between the sender and receiver");
        return repository.existsBySenderAndReceiver(ownerRepository.findById(sender_id).get(),
                ownerRepository.findById(receiver_id).get());
    }
}
