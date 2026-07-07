package dev.kushagra.devvault.repository;

import dev.kushagra.devvault.model.Secret;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SecretRepository extends JpaRepository<Secret,Long>
{
    Optional<Secret> findByName(String name);
}
