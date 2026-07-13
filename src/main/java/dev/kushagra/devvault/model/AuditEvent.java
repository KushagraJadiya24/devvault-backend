package dev.kushagra.devvault.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuditEvent implements Serializable {

    private Long userId;
    private String action;
    private String secretName;
    private String ipAddress;
}