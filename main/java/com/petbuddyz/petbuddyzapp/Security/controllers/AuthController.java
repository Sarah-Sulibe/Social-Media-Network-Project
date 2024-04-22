package com.petbuddyz.petbuddyzapp.Security.controllers;

import jakarta.validation.Valid;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.petbuddyz.petbuddyzapp.Owner.Owner;
import com.petbuddyz.petbuddyzapp.Owner.OwnerRepository;
import com.petbuddyz.petbuddyzapp.Security.Repository.RefreshTokenRepository;
import com.petbuddyz.petbuddyzapp.Security.jwt.JwtUtils;
import com.petbuddyz.petbuddyzapp.Security.jwt.RefreshToken;
import com.petbuddyz.petbuddyzapp.Security.payload.request.LoginRequest;
import com.petbuddyz.petbuddyzapp.Security.payload.request.SignupRequest;
import com.petbuddyz.petbuddyzapp.Security.payload.response.JwtResponse;
import com.petbuddyz.petbuddyzapp.Security.payload.response.MessageResponse;
import com.petbuddyz.petbuddyzapp.Security.services.UserDetailsImpl;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private static final Logger logger = LogManager.getLogger(AuthController.class);

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    OwnerRepository userRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        logger.info("Authenticating user: {}", loginRequest.getUsername());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        logger.info("User authenticated successfully: {}", userDetails.getUsername());

        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail()));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        logger.info("Registering user: {}", signUpRequest.getUsername());

        if (userRepository.existsByOwnerName(signUpRequest.getUsername())) {
            logger.error("Username is already taken: {}", signUpRequest.getUsername());
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByOwnerEmail(signUpRequest.getEmail())) {
            logger.error("Email is already in use: {}", signUpRequest.getEmail());
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create new user's account
        Owner user = new Owner(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));

        userRepository.save(user);

        logger.info("User registered successfully: {}", signUpRequest.getUsername());

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody Map<String, String> request) {
        logger.info("Refreshing token");

        String refreshToken = request.get("refreshToken");
        Optional<RefreshToken> refreshTokenEntity = refreshTokenRepository.findById(refreshToken);

        if (refreshTokenEntity.isPresent()) {
            RefreshToken refreshTokenEntity1 = refreshTokenEntity.get();
            if (refreshTokenEntity1.getExpiryDate().after(new Date())) {
                String username = refreshTokenEntity1.getUsername();
                Owner userDetails = userRepository.findByOwnerName(username).get();
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
                String jwt = jwtUtils.generateJwtToken(authentication);

                logger.info("Token refreshed successfully for user: {}", userDetails.getOwnerName());

                return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getOwnerId(), userDetails.getOwnerName(), userDetails.getOwnerEmail()));
            } else {
                refreshTokenRepository.delete(refreshTokenEntity1);

                logger.error("Refresh token expired");

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token expired");
            }
        } else {
            logger.error("Refresh token not found");

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token not found");
        }
    }

}