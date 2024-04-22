package com.petbuddyz.petbuddyzapp.Group;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<Group, Long> {
    // query methods...
}