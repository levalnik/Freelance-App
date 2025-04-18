package org.levalnik.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.levalnik.DTO.ProjectDTO;
import org.levalnik.enums.projectEnum.ProjectStatus;
import org.levalnik.kafkaEvent.projectKafkaEvent.*;
import org.levalnik.kafka.KafkaProducer;
import org.levalnik.model.Project;
import org.levalnik.kafkaEvent.projectKafkaEvent.ProjectCreatedEvent;
import org.levalnik.repository.ProjectRepository;
import org.levalnik.mapper.ProjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final KafkaProducer kafkaProducer;

    @Transactional(readOnly = true)
    public Page<ProjectDTO> getProjectsByStatus(ProjectStatus status, Pageable pageable) {
        log.info("Fetching projects with status: {}", status);
        Page<ProjectDTO> projects = projectRepository.findByStatus(status, pageable)
                .map(projectMapper::toDTO);
        log.info("Found {} projects with status {}", projects.getTotalElements(), status);
        return projects;
    }

    @Transactional(readOnly = true)
    public List<Project> getAllProjects() {
        log.info("Fetching all projects");
        List<Project> projects = projectRepository.findAll();
        log.info("Found {} projects", projects.size());
        return projects;
    }

    @Transactional(readOnly = true)
    public Optional<Project> getProjectById(UUID id) {
        log.info("Fetching project with ID: {}", id);
        Optional<Project> project = projectRepository.findById(id);
        if (project.isPresent()) {
            log.info("Project found: {}", project.get());
        } else {
            log.warn("Project with ID {} not found", id);
        }
        return project;
    }

    @Transactional(readOnly = true)
    public List<Project> getProjectsByClient(UUID clientId) {
        log.info("Fetching projects for client ID: {}", clientId);
        List<Project> projects = projectRepository.findByClientId(clientId);
        log.info("Found {} projects for client ID {}", projects.size(), clientId);
        return projects;
    }

    @Transactional
    public Project createProject(Project project) {
        log.info("Creating new project: {}", project);
        project.setCreatedAt(LocalDateTime.now());
        project.setUpdatedAt(LocalDateTime.now());
        project.setBidCount(0);
        Project savedProject = projectRepository.save(project);
        log.info("Project created with ID: {}", savedProject.getId());

        kafkaProducer.sendProjectCreatedEvent(
                ProjectCreatedEvent.builder()
                        .projectId(savedProject.getId())
                        .title(savedProject.getTitle())
                        .description(savedProject.getDescription())
                        .budget(savedProject.getBudget())
                        .clientId(savedProject.getClientId())
                        .status(savedProject.getStatus())
                        .createdAt(savedProject.getCreatedAt())
                        .build()
        );

        return savedProject;
    }

    @Transactional
    public Project updateProject(UUID id, Project updatedProject) {
        log.info("Updating project with ID: {}", id);
        return projectRepository.findById(id)
                .map(project -> {
                    log.info("Existing project: {}", project);
                    project.setTitle(updatedProject.getTitle());
                    project.setDescription(updatedProject.getDescription());
                    project.setBudget(updatedProject.getBudget());
                    project.setUpdatedAt(LocalDateTime.now());
                    Project savedProject = projectRepository.save(project);
                    log.info("Project updated: {}", savedProject);

                    kafkaProducer.sendProjectUpdatedEvent(
                            ProjectUpdatedEvent.builder()
                                    .projectId(savedProject.getId())
                                    .title(savedProject.getTitle())
                                    .description(savedProject.getDescription())
                                    .budget(savedProject.getBudget())
                                    .status(savedProject.getStatus())
                                    .updatedAt(savedProject.getUpdatedAt())
                                    .build()
                    );

                    return savedProject;
                })
                .orElseThrow(() -> {
                    log.error("Project with ID {} not found, update failed", id);
                    return new EntityNotFoundException("Project not found with ID: " + id);
                });
    }

    @Transactional
    public void deleteProject(UUID id) {
        log.info("Attempting to delete project with ID: {}", id);
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Project not found with ID: " + id));

        kafkaProducer.sendProjectDeletedEvent(
                ProjectDeletedEvent.builder()
                        .projectId(id)
                        .deletedAt(LocalDateTime.now())
                        .build()
        );

        projectRepository.deleteById(project.getId());
        log.info("Project with ID {} successfully deleted", id);
    }

    @Transactional
    public void closeProjectsByClient(UUID clientId) {
        log.info("Closing all projects for client: {}", clientId);
        List<Project> projects = projectRepository.findByClientId(clientId);

        projects.forEach(project -> {
            if (project.getStatus() != ProjectStatus.COMPLETED &&
                    project.getStatus() != ProjectStatus.CANCELLED) {

                project.setStatus(ProjectStatus.CANCELLED);
                project.setUpdatedAt(LocalDateTime.now());
                Project savedProject = projectRepository.save(project);

                kafkaProducer.sendProjectUpdatedEvent(
                        ProjectUpdatedEvent.builder()
                                .projectId(savedProject.getId())
                                .status(ProjectStatus.CANCELLED)
                                .updatedAt(savedProject.getUpdatedAt())
                                .reason("Client account deleted")
                                .build()
                );
            }
        });

        log.info("Closed {} projects for client: {}", projects.size(), clientId);
    }

    @Transactional
    public void updateBidCount(UUID projectId) {
        log.info("Updating bid count for project: {}", projectId);
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found: " + projectId));

        project.setBidCount(project.getBidCount() + 1);
        project.setUpdatedAt(LocalDateTime.now());
        projectRepository.save(project);

        log.info("Updated bid count for project: {}", projectId);
    }

    @Transactional
    public void updateStatus(UUID projectId, ProjectStatus status) {
        log.info("Updating project status for project: {}", projectId);
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found: " + projectId));
        project.setStatus(status);
        project.setUpdatedAt(LocalDateTime.now());
        Project savedProject = projectRepository.save(project);

        kafkaProducer.sendProjectUpdatedEvent(
                ProjectUpdatedEvent.builder()
                        .projectId(savedProject.getId())
                        .title(savedProject.getTitle())
                        .description(savedProject.getDescription())
                        .budget(savedProject.getBudget())
                        .status(savedProject.getStatus())
                        .updatedAt(LocalDateTime.now())
                        .build()
        );

        log.info("Updated status for project: {}", projectId);
    }
}