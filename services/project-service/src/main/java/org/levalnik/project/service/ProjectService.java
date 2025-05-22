package org.levalnik.project.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.levalnik.dto.projectDto.ProjectRequest;
import org.levalnik.dto.projectDto.ProjectResponse;
import org.levalnik.outbox.model.OutboxEvent;
import org.levalnik.outbox.repository.OutboxEventRepository;
import org.levalnik.enums.projectEnum.ProjectStatus;
import org.levalnik.kafkaEvent.projectKafkaEvent.*;
import org.levalnik.project.model.Project;
import org.levalnik.kafkaEvent.projectKafkaEvent.ProjectCreatedEvent;
import org.levalnik.project.repository.ProjectRepository;
import org.levalnik.project.mapper.ProjectMapper;
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
    private final ObjectMapper objectMapper;
    private final OutboxEventRepository outboxEventRepository;

    @Transactional(readOnly = true)
    public Page<ProjectResponse> getProjectsByStatus(ProjectStatus status, Pageable pageable) {
        log.info("Fetching projects with status: {}", status);
        Page<ProjectResponse> projects = projectRepository.findByStatus(status, pageable)
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
    public ProjectResponse createProject(ProjectRequest projectDTO) {
        if(projectRepository.existsById(projectDTO.getId())) {
            throw new IllegalArgumentException("Project with id " + projectDTO.getId() + " already exists");
        }

        Project project = projectMapper.toEntity(projectDTO);
        Project savedProject = projectRepository.save(project);

        String payload;
        try {
            payload = objectMapper.writeValueAsString(
                    ProjectCreatedEvent.builder()
                            .projectId(savedProject.getId())
                            .title(savedProject.getTitle())
                            .description(savedProject.getDescription())
                            .budget(savedProject.getBudget())
                            .status(savedProject.getStatus())
                            .createdAt(LocalDateTime.now())
                            .clientId(savedProject.getClientId())
                            .build()
            );
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize ProjectCreatedEvent for project ID: {}", savedProject.getId(), e);
            throw new RuntimeException("Failed to serialize event payload", e);
        }

        OutboxEvent event = OutboxEvent.builder()
                .aggregateType("Project")
                .aggregateId(savedProject.getId().toString())
                .eventType("ProjectCreatedEvent")
                .payload(payload)
                .createdAt(LocalDateTime.now())
                .build();
        outboxEventRepository.save(event);

        log.info("Created project with ID: {}", savedProject.getId());

        return projectMapper.toDTO(savedProject);
    }

    @Transactional
    public ProjectResponse updateProject(UUID id, ProjectRequest projectDTO) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Project with id " + id + " not found"));
        project.setTitle(projectDTO.getTitle());
        project.setDescription(projectDTO.getDescription());
        project.setBudget(projectDTO.getBudget());
        project.setStatus(projectDTO.getStatus());

        Project updatedProject = projectRepository.save(project);

        ProjectUpdatedEvent eventPayload = ProjectUpdatedEvent.builder()
                .projectId(updatedProject.getId())
                .title(updatedProject.getTitle())
                .description(updatedProject.getDescription())
                .budget(updatedProject.getBudget())
                .updatedAt(LocalDateTime.now())
                .build();

        try {
            String payload = objectMapper.writeValueAsString(eventPayload);

            OutboxEvent event = OutboxEvent.builder()
                    .aggregateType("Project")
                    .aggregateId(updatedProject.getId().toString())
                    .eventType("ProjectUpdatedEvent")
                    .payload(payload)
                    .createdAt(LocalDateTime.now())
                    .build();

            outboxEventRepository.save(event);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize ProjectUpdatedEvent for project ID: {}", updatedProject.getId(), e);
            throw new RuntimeException("Failed to serialize event payload", e);
        }

        log.info("Updated project with ID: {}", updatedProject.getId());
        return projectMapper.toDTO(updatedProject);
    }

    @Transactional
    public void deleteProject(UUID id) {
        log.info("Attempting to delete project with ID: {}", id);
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Project not found with ID: " + id));

        ProjectDeletedEvent eventPayload = ProjectDeletedEvent.builder()
                .projectId(id)
                .deletedAt(LocalDateTime.now())
                .build();

        try {
            String payload = objectMapper.writeValueAsString(eventPayload);

            OutboxEvent event = OutboxEvent.builder()
                    .aggregateType("Project")
                    .aggregateId(id.toString())
                    .eventType("ProjectDeletedEvent")
                    .payload(payload)
                    .createdAt(LocalDateTime.now())
                    .build();

            outboxEventRepository.save(event);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize ProjectDeletedEvent for project ID: {}", id, e);
            throw new RuntimeException("Failed to serialize event payload", e);
        }

        projectRepository.deleteById(id);
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

                ProjectUpdatedEvent eventPayload = ProjectUpdatedEvent.builder()
                        .projectId(savedProject.getId())
                        .status(ProjectStatus.CANCELLED)
                        .updatedAt(savedProject.getUpdatedAt())
                        .reason("Client account deleted")
                        .build();

                try {
                    String payload = objectMapper.writeValueAsString(eventPayload);

                    OutboxEvent event = OutboxEvent.builder()
                            .aggregateType("Project")
                            .aggregateId(savedProject.getId().toString())
                            .eventType("ProjectUpdatedEvent")
                            .payload(payload)
                            .createdAt(LocalDateTime.now())
                            .build();

                    outboxEventRepository.save(event);
                } catch (JsonProcessingException e) {
                    log.error("Failed to serialize ProjectUpdatedEvent for project ID: {}", savedProject.getId(), e);
                    throw new RuntimeException("Failed to serialize event payload", e);
                }
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

        ProjectUpdatedEvent eventPayload = ProjectUpdatedEvent.builder()
                .projectId(savedProject.getId())
                .title(savedProject.getTitle())
                .description(savedProject.getDescription())
                .budget(savedProject.getBudget())
                .status(savedProject.getStatus())
                .updatedAt(savedProject.getUpdatedAt())
                .build();

        try {
            String payload = objectMapper.writeValueAsString(eventPayload);

            OutboxEvent event = OutboxEvent.builder()
                    .aggregateType("Project")
                    .aggregateId(savedProject.getId().toString())
                    .eventType("ProjectUpdatedEvent")
                    .payload(payload)
                    .createdAt(LocalDateTime.now())
                    .build();

            outboxEventRepository.save(event);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize ProjectUpdatedEvent for project ID: {}", savedProject.getId(), e);
            throw new RuntimeException("Failed to serialize event payload", e);
        }

        log.info("Updated status for project: {}", projectId);
    }
}