package org.levalnik.dto.user;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.levalnik.enums.userEnum.UserPermission;
import org.levalnik.enums.userEnum.UserRole;
import org.levalnik.enums.userEnum.UserStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
public class UserRegisterRequest {

    @NotBlank
    @Size(min = 3, max = 50)
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username can only contain letters, digits, and underscores")
    private String username;

    @NotBlank(message = "Email is mandatory")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "First name is required")
    @Size(min = 1, max = 50)
    private String firstName;

    @NotBlank(message = "Second name is required")
    @Size(min = 1, max = 50)
    private String lastName;

    @NotBlank(message = "Phone number is required")
    @Size(min = 10, max = 15)
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Phone number must be between 10 and 15 digits, and may start with a '+'")
    private String phoneNumber;

    @NotBlank(message = "Passport is mandatory")
    @Size(min = 8)
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).{8,}$", message = "Password must contain at least one uppercase letter, one lowercase letter, and one digit")
    private String password;

    @NotNull
    private UserRole role;

    @NotNull
    private Set<UserPermission> permissions;

    @NotNull
    private UserStatus status;

    @PastOrPresent
    private LocalDateTime dateOfBirth;
}
