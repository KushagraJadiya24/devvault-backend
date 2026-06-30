package dev.kushagra.devvault.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "Email Cannot be Empty")
    @Email(message="Enter a valid Email Address")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;
}
