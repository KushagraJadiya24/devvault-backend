package dev.kushagra.devvault.repository;

import dev.kushagra.devvault.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findByEmail(String email); 

}
