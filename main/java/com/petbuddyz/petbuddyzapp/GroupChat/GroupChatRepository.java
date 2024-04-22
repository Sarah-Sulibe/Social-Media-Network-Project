package com.petbuddyz.petbuddyzapp.GroupChat;


import org.springframework.data.jpa.repository.JpaRepository;



public interface  GroupChatRepository extends JpaRepository<GroupChat, Long> {
    // You can define custom query methods here if needed
}