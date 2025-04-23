package org.levalnik.project.repository;

import org.levalnik.enums.projectEnum.ProjectStatus;
import org.levalnik.project.model.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProjectRepository extends JpaRepository<Project, UUID> {
    List<Project> findByClientId(UUID userId);

    Page<Project> findByStatus(ProjectStatus status, Pageable pageable);
}