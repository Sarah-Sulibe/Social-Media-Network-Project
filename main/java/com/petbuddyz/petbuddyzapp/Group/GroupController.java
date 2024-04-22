package com.petbuddyz.petbuddyzapp.Group;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.petbuddyz.petbuddyzapp.Owner.Owner;
import com.petbuddyz.petbuddyzapp.Security.services.UserDetailsImpl;
import org.springframework.security.core.Authentication;

/**
 * The controller class for managing groups.
 */
@RestController
@RequestMapping("/groups")
public class GroupController {
    // endpoints...
    private static final Logger logger = LoggerFactory.getLogger(GroupController.class);
    private final GroupService groupService;
    private final GroupModelAssembler groupModelAssembler;

    public GroupController(GroupService groupService, GroupModelAssembler groupModelAssembler) {
        this.groupService = groupService;
        this.groupModelAssembler = groupModelAssembler;
    }

    private Long getAuthenticatedOwnerId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userDetails.getId();
    }
    @GetMapping
    public CollectionModel<EntityModel<Group>> allGroups() {
        try {
            Collection<EntityModel<Group>> groups = groupService.all().getContent();
            return CollectionModel.of(groups, linkTo(methodOn(GroupController.class).allGroups()).withSelfRel());
        } catch (Exception ex) {
            logger.error("Error while retrieving groups: {}", ex.getMessage());
            throw new RuntimeException("Error while retrieving groups", ex);
        }
    }
    
    

    @GetMapping("/{groupId}")
    public EntityModel<Group> getGroup(@PathVariable Long groupId) throws Exception {
        Group group = groupService.one(groupId).getContent(); // Assuming getContent() retrieves the group object
        return groupModelAssembler.toModel(group);
    }

    @PostMapping("/create")
    public ResponseEntity<EntityModel<Group>> creatGroup(@RequestParam Long groupMakerId,@RequestParam  String groupName) throws Exception {
        Long authenticatedOwnerId = getAuthenticatedOwnerId(); 
        Group group = groupService.createGroup(authenticatedOwnerId, groupName);
        return ResponseEntity.created(linkTo(methodOn(GroupController.class).getGroup(group.getGroupId())).toUri())
                .body(groupModelAssembler.toModel(group));
    }

    @PostMapping("/{groupId}/join")
    public ResponseEntity<EntityModel<Group>> joinGroup(@PathVariable Long groupId, @RequestBody Long ownerId,@RequestParam  String groupName) throws Exception {

        Long authenticatedOwnerId = getAuthenticatedOwnerId(); 
        Group group = groupService.joinGroup(groupId, groupName, authenticatedOwnerId);
        return ResponseEntity.created(linkTo(methodOn(GroupController.class).getGroup(group.getGroupId())).toUri())
                .body(groupModelAssembler.toModel(group));
    }

    @DeleteMapping("/{groupId}/members/{memberId}")
    public ResponseEntity<EntityModel<Group>> deleteMember(@PathVariable Long groupId, @PathVariable Long memberId) {
        Long authenticatedOwnerId = getAuthenticatedOwnerId(); 
        Group group = groupService.RemoveMemberfromGroup(groupId, authenticatedOwnerId);
        if (group != null) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }



@PutMapping("/{groupId}/members/{memberId}")
    public ResponseEntity<Group> addMemberToGroup(@PathVariable Long groupId, @PathVariable Long memberId) {
        try {
            Group updatedGroup = groupService.AddMembertoGroup(groupId, memberId);
            return ResponseEntity.ok(updatedGroup);
        } catch (Exception ex) {
            // Handle exception
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }





    @DeleteMapping("/{groupId}")
    public ResponseEntity<Void>  deleteGroup(@PathVariable Long groupId,@RequestParam Long requesterId) {
        groupService.deleteGroup(groupId, requesterId);
        return ResponseEntity.noContent().build();
    }

}