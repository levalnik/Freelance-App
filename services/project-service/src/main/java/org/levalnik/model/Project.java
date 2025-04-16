package org.levalnik.model;

import jakarta.persistence.*;
import lombok.*;
import org.levalnik.enums.projectEnum.ProjectStatus;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "projects")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    @NotNull
    @Size(min = 3, max = 100)
    private String title;

    @Column(columnDefinition = "TEXT")
    @Size(max = 2000)
    private String description;

    @Column(nullable = false)
    @NotNull
    @Positive
    private Double budget;

    @Column(nullable = false)
    @NotNull
    private UUID clientId;

    @Column(nullable = false)
    @NotNull
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    @NotNull
    @Enumerated(EnumType.STRING)
    private ProjectStatus status;

    @Column(nullable = false)
    @NotNull
    private Integer bidCount;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        bidCount = 0;
        status = ProjectStatus.OPEN;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}