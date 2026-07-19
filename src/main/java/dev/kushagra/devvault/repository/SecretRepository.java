package dev.kushagra.devvault.repository;

import dev.kushagra.devvault.model.Secret;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SecretRepository extends JpaRepository<Secret,Long>
{
    Optional<Secret> findByName(String name);
    // find the current active version
    Optional<Secret> findByNameAndActiveTrue(String name);

    // find all versions sorted by version number descending
    List<Secret> findByNameOrderByVersionDesc(String name);

    // find a specific version
    Optional<Secret> findByNameAndVersion(String name, Integer version);
}
