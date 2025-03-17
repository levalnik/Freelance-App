package org.levalnik.DTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProjectDTO {

    private Long id;

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "Budget is required")
    @Min(value = 1, message = "Budget must be greater than zero")
    private Double budget;

    @NotNull(message = "Client ID is required")
    private Long clientId;
}