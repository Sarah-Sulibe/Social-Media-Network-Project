package com.petbuddyz.petbuddyzapp.GroupChat;


import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component

public class GroupChatModelAssembler implements RepresentationModelAssembler<GroupChat, EntityModel<GroupChat>> {


     @Override
    public EntityModel<GroupChat> toModel(GroupChat chat) {
       return EntityModel.of(chat, //
        linkTo(methodOn(GroupChatController.class).getGroupChatById(chat.getGroupChatId())).withSelfRel(),
        linkTo(methodOn(GroupChatController.class).getAllGroupChats()).withRel("groupChats"));
    }
    
}