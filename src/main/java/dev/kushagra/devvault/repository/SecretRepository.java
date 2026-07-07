package dev.kushagra.devvault.repository;

import dev.kushagra.devvault.model.Secret;

import java.util.Optional;

public interface SecretRepository
{
    Optional<Secret> findByName(String name);
}
