package dev.kushagra.devvault.service;

import dev.kushagra.devvault.dto.SecretRequest;
import dev.kushagra.devvault.model.Environment;
import dev.kushagra.devvault.model.Secret;
import dev.kushagra.devvault.repository.SecretRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SecretService {

    private final AuditService auditService;
    private final SecretRepository secretRepository;
    private final AesEncryptionService aesEncryptionService;

    public Secret createSecret(SecretRequest request, Long userId, String ipAddress) throws Exception {
        Secret secret = new Secret();
        secret.setName(request.getName());
        secret.setProjectId(request.getProjectId());
        secret.setEnvironment(request.getEnvironment());
        secret.setCreatedBy(userId);
        secret.setVersion(1);
        secret.setActive(true);
        secret.setEncryptedValue(aesEncryptionService.encrypt(request.getValue()));
        auditService.publishEvent(userId, "CREATE", request.getName(), ipAddress);
        return secretRepository.save(secret);
    }

    @Cacheable(value = "secrets", key = "#name + '_' + #projectId")
    public String getSecretByName(String name, Long projectId, Long userId, String ipAddress) throws Exception {
        Secret secret = secretRepository.findByNameAndProjectIdAndActiveTrue(name, projectId)
                .orElseThrow(() -> new RuntimeException("Secret not found"));
        auditService.publishEvent(userId, "READ", name, ipAddress);
        return aesEncryptionService.decrypt(secret.getEncryptedValue());
    }

    @CacheEvict(value = "secrets", key = "#name + '_' + #projectId")
    public Secret updateSecret(String name, Long projectId, String newValue, Long userId, String ipAddress) throws Exception {
        Secret current = secretRepository.findByNameAndProjectIdAndActiveTrue(name, projectId)
                .orElseThrow(() -> new RuntimeException("Secret not found"));
        current.setActive(false);
        secretRepository.save(current);

        Secret newSecret = new Secret();
        newSecret.setName(current.getName());
        newSecret.setProjectId(current.getProjectId());
        newSecret.setEnvironment(current.getEnvironment());
        newSecret.setCreatedBy(userId);
        newSecret.setVersion(current.getVersion() + 1);
        newSecret.setActive(true);
        newSecret.setEncryptedValue(aesEncryptionService.encrypt(newValue));
        auditService.publishEvent(userId, "UPDATE", name, ipAddress);
        return secretRepository.save(newSecret);
    }

    @CacheEvict(value = "secrets", key = "#name + '_' + #projectId")
    public void deleteSecret(String name, Long projectId, Long userId, String ipAddress) {
        Secret secret = secretRepository.findByNameAndProjectIdAndActiveTrue(name, projectId)
                .orElseThrow(() -> new RuntimeException("Secret not found"));
        auditService.publishEvent(userId, "DELETE", name, ipAddress);
        secretRepository.delete(secret);
    }

    public List<Secret> getSecretsByProject(Long projectId) {
        return secretRepository.findByProjectIdAndActiveTrue(projectId);
    }

    public List<Secret> getSecretsByProjectAndEnvironment(Long projectId, Environment environment) {
        return secretRepository.findByProjectIdAndEnvironmentAndActiveTrue(projectId, environment);
    }

    public List<Secret> getSecretHistory(String name, Long projectId) {
        return secretRepository.findByNameAndProjectIdOrderByVersionDesc(name, projectId);
    }

    public String getSecretByVersion(String name, Long projectId, Integer version) throws Exception {
        Secret secret = secretRepository.findByNameAndProjectIdAndVersion(name, projectId, version)
                .orElseThrow(() -> new RuntimeException("Version not found"));
        return aesEncryptionService.decrypt(secret.getEncryptedValue());
    }

    public Page<Secret> getAllSecrets(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return secretRepository.findAll(pageable);
    }
}