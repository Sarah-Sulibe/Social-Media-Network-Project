package com.petbuddyz.petbuddyzapp.Chat;

import org.springframework.data.jpa.repository.JpaRepository;

import com.petbuddyz.petbuddyzapp.Owner.Owner;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    boolean existsBySenderAndReceiver(Owner owner, Owner owner2);
    // query methods...
}