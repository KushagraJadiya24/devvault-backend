package dev.kushagra.devvault.repository;

import dev.kushagra.devvault.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByCreatedBy(Long createdBy);
}