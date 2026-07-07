package dev.kushagra.devvault.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SecretRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String value;

    @NotBlank
    private String projectId;
}
