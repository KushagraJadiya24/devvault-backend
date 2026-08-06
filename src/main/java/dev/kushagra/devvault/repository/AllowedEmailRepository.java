package dev.kushagra.devvault.repository;

import dev.kushagra.devvault.model.AllowedEmail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AllowedEmailRepository extends JpaRepository<AllowedEmail, Long> {
    Optional<AllowedEmail> findByEmail(String email);
    boolean existsByEmail(String email);
}