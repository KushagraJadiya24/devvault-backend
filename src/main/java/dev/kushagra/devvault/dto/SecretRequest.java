package dev.kushagra.devvault.dto;

import dev.kushagra.devvault.model.Environment;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SecretRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Value is required")
    private String value;

    @NotNull(message = "Project ID is required")
    private Long projectId;

    @NotNull(message = "Environment is required")
    private Environment environment;
}