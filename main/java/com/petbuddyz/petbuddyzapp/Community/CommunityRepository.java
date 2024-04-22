package com.petbuddyz.petbuddyzapp.Community;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityRepository extends JpaRepository<Community, Long> {

    public List<Community> findAll();

}