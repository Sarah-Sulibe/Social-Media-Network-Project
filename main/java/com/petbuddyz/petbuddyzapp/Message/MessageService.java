package com.petbuddyz.petbuddyzapp.Message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.petbuddyz.petbuddyzapp.Message.Message.Status;
import com.petbuddyz.petbuddyzapp.Post.Post;
import com.petbuddyz.petbuddyzapp.Post.PostService;

/**
 * The MessageService class is responsible for handling operations related to messages.
 * It provides methods to save, retrieve, delete, and create messages.
 */
@Service
public class MessageService {

    @Autowired
    private final MessageRepository messageRepository;

    @Autowired
    private PostService postService;

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public Message save(Message message) {
        return messageRepository.save(message);
    }

    public Message findById(Long id) {
        return messageRepository.findById(id).orElseThrow(() -> new MessageNotFoundException("Message not found with ID" +id));
    }

    public void deleteById(Long id) {
        messageRepository.deleteById(id);
    }

    public Message createMessage(MessageDto messageDto) {
        Message message = new Message();
        message.setContent(messageDto.getContent());
        if (messageDto.getMedia() != null) {
            message.setMedia(messageDto.getMedia());
        }
        if (messageDto.getPostId() != null) {
            Post post = postService.getPost(messageDto.getPostId());
            message.setPost(post);
        }
        message.setStatus(Status.UNREAD);
        return messageRepository.save(message);
    }
}