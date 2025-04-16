package org.levalnik.model;

import org.levalnik.enums.userEnum.UserPermission;
import org.levalnik.enums.userEnum.UserRole;
import org.levalnik.enums.userEnum.UserStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    @NotNull
    @Size(min = 3, max = 50)
    private String username;

    @Column(nullable = false, unique = true)
    @NotNull
    @Email
    private String email;

    @Column(nullable = false)
    @NotNull
    @Size(min = 1, max = 50)
    private String firstName;

    @Column(nullable = false)
    @NotNull
    @Size(min = 1, max = 50)
    private String lastName;

    @Column(nullable = false, unique = true)
    @NotNull
    @Size(min = 10, max = 15)
    private String phoneNumber;

    @Column(nullable = false)
    @NotNull
    @Size(min = 8)
    private String password;

    @Column(nullable = false)
    @NotNull
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column(nullable = false)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_permissions", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @NotNull
    private Set<UserPermission> permissions;

    @Column(nullable = false)
    @NotNull
    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @Column
    @PastOrPresent
    private LocalDateTime dateOfBirth;

    @Column(nullable = false)
    @NotNull
    private LocalDateTime registrationDate;

    @Column
    private LocalDateTime lastLoginDate;

    @Column(nullable = false)
    @NotNull
    private Integer projectCount;

    @Column(nullable = false)
    @NotNull
    private Integer bidCount;

    @PrePersist
    protected void onCreate() {
        registrationDate = LocalDateTime.now();
        projectCount = 0;
        bidCount = 0;
        status = UserStatus.ACTIVE;
    }

    @PreUpdate
    protected void onUpdate() {
        lastLoginDate = LocalDateTime.now();
    }
}