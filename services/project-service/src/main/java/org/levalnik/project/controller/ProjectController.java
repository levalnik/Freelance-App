package org.levalnik.project.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.levalnik.dto.projectDto.ProjectRequest;
import org.levalnik.dto.projectDto.ProjectResponse;
import org.levalnik.enums.projectEnum.ProjectStatus;
import org.levalnik.project.model.Project;
import org.levalnik.project.service.ProjectService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping("/status/{status}")
    public ResponseEntity<Page<ProjectResponse>> getProjectsByStatus(@PathVariable ProjectStatus status, Pageable pageable) {
        log.info("Fetching projects with status: {}", status);
        return ResponseEntity.ok(projectService.getProjectsByStatus(status, pageable));
    }

    @GetMapping
    public ResponseEntity<List<Project>> getAllProjects() {
        log.info("Fetching all projects");
        return ResponseEntity.ok(projectService.getAllProjects());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Project> getProjectById(@PathVariable UUID id) {
        log.info("Fetching project with ID: {}", id);
        return projectService.getProjectById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<Project>> getProjectsByClient(@PathVariable UUID clientId) {
        log.info("Fetching projects for client ID: {}", clientId);
        return ResponseEntity.ok(projectService.getProjectsByClient(clientId));
    }

    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(@RequestBody ProjectRequest projectDTO) {
        log.info("Creating new project");
        return ResponseEntity.ok(projectService.createProject(projectDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectResponse> updateProject(@PathVariable UUID id, @RequestBody ProjectRequest projectDTO) {
        log.info("Updating project with ID: {}", id);
        return ResponseEntity.ok(projectService.updateProject(id, projectDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable UUID id) {
        log.info("Deleting project with ID: {}", id);
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/client/{clientId}/close")
    public ResponseEntity<Void> closeProjectsByClient(@PathVariable UUID clientId) {
        log.info("Closing all projects for client ID: {}", clientId);
        projectService.closeProjectsByClient(clientId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/bid-count")
    public ResponseEntity<Void> updateBidCount(@PathVariable UUID id) {
        log.info("Updating bid count for project ID: {}", id);
        projectService.updateBidCount(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(@PathVariable UUID id, ProjectStatus status) {
        log.info("Updating status for project ID: {}", id);
        projectService.updateStatus(id, status);
        return ResponseEntity.ok().build();
    }
}