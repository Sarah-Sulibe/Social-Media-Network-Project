package com.petbuddyz.petbuddyzapp.Group;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Service;

import com.petbuddyz.petbuddyzapp.Notification.NotificationService;
import com.petbuddyz.petbuddyzapp.Owner.Owner;
import com.petbuddyz.petbuddyzapp.Owner.OwnerNotFoundException;
import com.petbuddyz.petbuddyzapp.Owner.OwnerRepository;
import com.petbuddyz.petbuddyzapp.Notification.Notification.NotificationType;
/**
 * The GroupService class provides methods for managing groups and their members.
 */
@Service
public class GroupService {
    // service methods...

    private static final Logger logger = LoggerFactory.getLogger(GroupService.class);

    @Autowired
    private GroupRepository repository;

    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private GroupModelAssembler assembler;

    @Autowired
    private NotificationService notificationService;



     
    //get all groups
    public CollectionModel<EntityModel<Group>> all() throws Exception {
        logger.info("Getting groups");
        try {
            List<EntityModel<Group>> groups = repository.findAll().stream() //
                .map(assembler::toModel) //
                .collect(Collectors.toList());

            return CollectionModel.of(groups, linkTo(methodOn(GroupController.class).allGroups()).withSelfRel());
        } catch (Exception ex) {
            // TODO: handle exception
            logger.error("Error while getting groups: {}", ex.getMessage());
            throw ex;
        }
    }

    //get one Group
    public EntityModel<Group> one(Long groupId) throws Exception {
        logger.info("Getting a Group");
        try {
            // Retrieve likes from the repository in a safe manner
            Group group = repository.findById(groupId) //
                .orElseThrow(() -> new GroupNotFoundException("Group not found with ID: " + groupId));

            return assembler.toModel(group);
        } catch (GroupNotFoundException ex) {
            // Log the specific exception message
            logger.error("Group not found with ID {}: {}", groupId, ex.getMessage());
            throw ex;
        }
    }


    public Group createGroup(Long groupMakerId, String groupName) {
        logger.info("Creating a group via an owner with ID: {}", groupMakerId);

        Group group = new Group();
      


        Optional<Owner> ownerOptional = ownerRepository.findById(groupMakerId);
        Owner groupMaker = ownerOptional.orElseThrow(() -> new OwnerNotFoundException("Owner not found with ID: " + groupMakerId));
        group.setGroupName(groupName);
        group.setGroupMaker(groupMaker);
        // Save the like using the repository
        Group savedGroup = repository.save(group);
        logger.info("return the group");
        return savedGroup;
    }

    public Group joinGroup(Long groupId, String groupName, Long ownerId) {
        logger.info("Joining a group");

        Optional<Owner> ownerOptional = ownerRepository.findById(ownerId);
        Owner owner = ownerOptional.orElseThrow(() -> new OwnerNotFoundException("Owner not found with ID: " + ownerId));

        Optional<Group> groupOptional = repository.findById(groupId);
        Group group = groupOptional.orElseThrow(() -> new GroupNotFoundException("Group not found with id: " + groupId));

        // Add the owner to the group members
        Set<Owner> members = group.getMembers();
        if (members == null) {
            members = new HashSet<>();
        }
        members.add(owner);
        group.setMembers(members);
        return group;
    }

    public void deleteGroup(Long groupId, Long requesterId) {
        logger.info("Finding the group to delete");
        Optional<Group> groupOptional = repository.findById(groupId);
        Group group = groupOptional.orElseThrow(() -> new GroupNotFoundException("Group not found with id: " + groupId));


// Check if the requester is the group maker
if (!group.getGroupMaker().getOwnerId().equals(requesterId)) {
    throw new UnauthorizedAccessException("Only the group maker can delete the group");
}
        logger.info("Deleting the group");
        repository.delete(group);
    }

    public Group AddMembertoGroup(Long groupId, Long memberId) {
        Set<Owner> owners =new HashSet<>();

        logger.info("Finding the group");
        Optional<Group> groupOptional = repository.findById(groupId);
        Group group = groupOptional.orElseThrow(() -> new GroupNotFoundException("Group not found with id: " + groupId));

        Optional<Owner> ownerOptional = ownerRepository.findById(memberId);
        Owner owner = ownerOptional.orElseThrow(() -> new OwnerNotFoundException("Owner not found with ID: " + memberId));

        owners.add(owner);

        group.setMembers(owners);

        // Notify group members about the new member joining the group
        String message = "New member joined the group: " + owner.getOwnerName();
        NotificationType type = NotificationType.GROUP_JOIN;
        for (Owner member : group.getMembers()) {
            if (!member.getOwnerId().equals(owner.getOwnerId())) { // Exclude the member who joined the group
                notificationService.sendNotification(member.getOwnerId(), message, type);
            }
        }
        return group;
    }

    public Group RemoveMemberfromGroup(Long groupId, Long memberId) {
        logger.info("Finding the group");
        Optional<Group> groupOptional = repository.findById(groupId);
        Group group = groupOptional.orElseThrow(() -> new GroupNotFoundException("Group not found with id: " + groupId));

        Optional<Owner> ownerOptional = ownerRepository.findById(memberId);
        Owner owner = ownerOptional.orElseThrow(() -> new OwnerNotFoundException("Owner not found with ID: " + memberId));

        Set<Owner> members = group.getMembers();

        members.remove(owner);
        group.setMembers(members);

        logger.info("Deleting the member from the group");
        return group;
    }

}