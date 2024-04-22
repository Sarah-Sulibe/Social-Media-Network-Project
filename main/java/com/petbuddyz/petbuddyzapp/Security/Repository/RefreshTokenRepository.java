package com.petbuddyz.petbuddyzapp.Security.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.petbuddyz.petbuddyzapp.Security.jwt.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
}
