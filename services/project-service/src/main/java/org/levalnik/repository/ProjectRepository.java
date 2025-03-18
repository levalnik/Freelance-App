package org.levalnik.repository;

import org.levalnik.model.Project;
import org.levalnik.model.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByClientId(Long clientId);

    Page<Project> findByStatus(Status status, Pageable pageable);
}