package org.levalnik.dto.project;

import lombok.Data;

import java.util.UUID;

@Data
public class ProjectResponse {
    private UUID projectId;
    private String title;
    private String description;
    private UUID ownerId;
}
