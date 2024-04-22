package com.petbuddyz.petbuddyzapp.Owner;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OwnerRepository extends JpaRepository<Owner, Long> {
    Optional<Owner> findByOwnerName(String ownerName);

    Optional<Owner> findByOwnerEmail(String ownerEmail);

    Optional<Owner> findByOwnerNameOrOwnerEmail(String ownerName, String ownerEmail);

    Boolean existsByOwnerName(String ownerName);

    Boolean existsByOwnerEmail(String ownerEmail);

    @Query("SELECT o.followers FROM Owner o WHERE o.ownerId = :id")
    List<Owner> getFollowers(@Param("id") Long id);

    @Query("SELECT o.following FROM Owner o WHERE o.ownerId = :id")
    List<Owner> getFollowing(@Param("id") Long id);
}