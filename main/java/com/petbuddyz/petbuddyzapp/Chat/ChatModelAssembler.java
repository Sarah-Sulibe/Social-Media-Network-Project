package com.petbuddyz.petbuddyzapp.Chat;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.petbuddyz.petbuddyzapp.Like.LikeModelAssembler;

@Component
public class ChatModelAssembler implements RepresentationModelAssembler<Chat, EntityModel<Chat>> {
 private static final Logger logger = LoggerFactory.getLogger(LikeModelAssembler.class);

    @Override
    public EntityModel<Chat> toModel(Chat chat) {
        
       try {
        return EntityModel.of(chat, //
            linkTo(methodOn(ChatController.class).one(chat.getChatId())).withSelfRel(),
            linkTo(methodOn(ChatController.class).all()).withRel("chats"));
    } catch (Exception e) {
        logger.error("Error while creating Chat entity model: {}", e.getMessage());
        throw new RuntimeException("Error while creating Chat entity model", e);
    }
    }
    
}

