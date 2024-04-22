package com.petbuddyz.petbuddyzapp.Message;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.petbuddyz.petbuddyzapp.Chat.ChatController;

@Component
public class MessageModelAssembler implements RepresentationModelAssembler<Message, EntityModel<Message>> {

    private static final Logger logger = LoggerFactory.getLogger(MessageModelAssembler.class);

    @Override
    public EntityModel<Message> toModel(Message message) {

        try {
            return EntityModel.of(message, //
                    linkTo(methodOn(ChatController.class).one(message.getMessageId())).withSelfRel(),
                    linkTo(methodOn(ChatController.class).all()).withRel("messages"));
        } catch (Exception e) {

            logger.error("Error while creating Message entity model: {}", e.getMessage());
            throw new RuntimeException("Error while creating Message entity model", e);
        }
    }

}
