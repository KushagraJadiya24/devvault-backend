package dev.kushagra.devvault.service;

import dev.kushagra.devvault.model.Environment;
import dev.kushagra.devvault.model.Secret;
import dev.kushagra.devvault.repository.SecretRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EnvService {

    private final SecretRepository secretRepository;
    private final AesEncryptionService aesEncryptionService;
    private final AuditService auditService;

    public List<Secret> importEnvFile(MultipartFile file, Long projectId,
                                      Environment environment, Long userId,
                                      String ipAddress) throws Exception {
        List<Secret> created = new ArrayList<>();
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream()));

        String line;
        while ((line = reader.readLine()) != null) {
            line = line.trim();

            // skip empty lines and comments
            if (line.isEmpty() || line.startsWith("#")) continue;

            // split on first = only
            int equalsIndex = line.indexOf('=');
            if (equalsIndex == -1) continue;

            String key = line.substring(0, equalsIndex).trim();
            String value = line.substring(equalsIndex + 1).trim();

            if (key.isEmpty() || value.isEmpty()) continue;

            // check if secret already exists in this project
            boolean exists = secretRepository
                    .findByNameAndProjectIdAndActiveTrue(key, projectId)
                    .isPresent();

            if (exists) continue; // skip duplicates

            Secret secret = new Secret();
            secret.setName(key);
            secret.setProjectId(projectId);
            secret.setEnvironment(environment);
            secret.setCreatedBy(userId);
            secret.setVersion(1);
            secret.setActive(true);
            secret.setEncryptedValue(aesEncryptionService.encrypt(value));

            secretRepository.save(secret);
            auditService.publishEvent(userId, "IMPORT", key, ipAddress);
            created.add(secret);
        }

        return created;
    }

    public String exportEnvFile(Long projectId, Environment environment) throws Exception {
        List<Secret> secrets = secretRepository
                .findByProjectIdAndEnvironmentAndActiveTrue(projectId, environment);

        StringBuilder sb = new StringBuilder();
        sb.append("# DevVault Export\n");
        sb.append("# Project ID: ").append(projectId).append("\n");
        sb.append("# Environment: ").append(environment).append("\n\n");

        for (Secret secret : secrets) {
            String decrypted = aesEncryptionService.decrypt(secret.getEncryptedValue());
            sb.append(secret.getName()).append("=").append(decrypted).append("\n");
        }

        return sb.toString();
    }
}