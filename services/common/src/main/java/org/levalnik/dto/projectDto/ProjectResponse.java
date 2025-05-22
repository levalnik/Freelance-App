package org.levalnik.dto.projectDto;

import lombok.Data;

import java.util.UUID;

@Data
public class ProjectResponse {
    private UUID id;
    private String title;
    private String description;
    private UUID ownerId;
}
