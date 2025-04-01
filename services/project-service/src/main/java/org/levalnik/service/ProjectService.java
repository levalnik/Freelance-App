package org.levalnik.service;

import lombok.extern.slf4j.Slf4j;
import org.levalnik.DTO.ProjectDTO;
import org.levalnik.DTO.events.ProjectCreatedEvent;
import org.levalnik.DTO.events.ProjectDeletedEvent;
import org.levalnik.DTO.events.ProjectUpdatedEvent;
import org.levalnik.model.Project;
import org.levalnik.model.enums.Status;
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
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final KafkaProducerService kafkaProducerService;

    @Transactional(readOnly = true)
    public Page<ProjectDTO> getProjectsByStatus(Status status, Pageable pageable) {
        log.info("Fetching projects with status: {}", status);
        Page<ProjectDTO> projects = projectRepository.findByStatus(status, pageable)
                .map(projectMapper::toDTO);
        log.info("Found {} projects with status {}", projects.getTotalElements(), status);
        return projects;
    }

    public List<Project> getAllProjects() {
        log.info("Fetching all projects");
        List<Project> projects = projectRepository.findAll();
        log.info("Found {} projects", projects.size());
        return projects;
    }

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

    public List<Project> getProjectsByClient(UUID clientId) {
        log.info("Fetching projects for client ID: {}", clientId);
        List<Project> projects = projectRepository.findByClientId(clientId);
        log.info("Found {} projects for client ID {}", projects.size(), clientId);
        return projects;
    }

    @Transactional
    public Project createProject(Project project) {
        log.info("Creating new project: {}", project);
        Project savedProject = projectRepository.save(project);
        log.info("Project created with ID: {}", savedProject.getId());
        
        kafkaProducerService.sendProjectCreatedEvent(
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
                    Project savedProject = projectRepository.save(project);
                    log.info("Project updated: {}", savedProject);
                    
                    kafkaProducerService.sendProjectUpdatedEvent(
                        ProjectUpdatedEvent.builder()
                            .projectId(savedProject.getId())
                            .title(savedProject.getTitle())
                            .description(savedProject.getDescription())
                            .budget(savedProject.getBudget())
                            .status(savedProject.getStatus())
                            .updatedAt(LocalDateTime.now())
                            .build()
                    );
                    
                    return savedProject;
                })
                .orElseThrow(() -> {
                    log.error("Project with ID {} not found, update failed", id);
                    return new RuntimeException("Project not found");
                });
    }

    @Transactional
    public void deleteProject(UUID id) {
        log.info("Attempting to delete project with ID: {}", id);
        if (!projectRepository.existsById(id)) {
            log.error("Project with ID {} not found, delete failed", id);
            throw new RuntimeException("Project not found");
        }
        
        kafkaProducerService.sendProjectDeletedEvent(
            ProjectDeletedEvent.builder()
                .projectId(id)
                .deletedAt(LocalDateTime.now())
                .build()
        );
        
        projectRepository.deleteById(id);
        log.info("Project with ID {} successfully deleted", id);
    }

    @Transactional
    public void closeProjectsByClient(UUID clientId) {
        log.info("Closing all projects for client: {}", clientId);
        List<Project> projects = projectRepository.findByClientId(clientId);
        
        for (Project project : projects) {
            project.setStatus(Status.CLOSED);
            Project savedProject = projectRepository.save(project);
            
            kafkaProducerService.sendProjectUpdatedEvent(
                ProjectUpdatedEvent.builder()
                    .projectId(savedProject.getId())
                    .title(savedProject.getTitle())
                    .description(savedProject.getDescription())
                    .budget(savedProject.getBudget())
                    .status(savedProject.getStatus())
                    .updatedAt(LocalDateTime.now())
                    .build()
            );
        }
        
        log.info("Closed {} projects for client: {}", projects.size(), clientId);
    }
}