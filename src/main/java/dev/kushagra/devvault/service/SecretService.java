package dev.kushagra.devvault.service;

import dev.kushagra.devvault.dto.SecretRequest;
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
        secret.setCreatedBy(userId);
        secret.setVersion(1);
        secret.setActive(true);
        secret.setEncryptedValue(aesEncryptionService.encrypt(request.getValue()));
        auditService.publishEvent(userId, "CREATE", request.getName(), ipAddress);
        return secretRepository.save(secret);
    }

    @Cacheable(value = "secrets", key = "#name")
    public String getSecretByName(String name, Long userId, String ipAddress) throws Exception {
        Secret secret = secretRepository.findByNameAndActiveTrue(name)
                .orElseThrow(() -> new RuntimeException("Secret not found"));
        auditService.publishEvent(userId, "READ", name, ipAddress);
        return aesEncryptionService.decrypt(secret.getEncryptedValue());
    }

    @CacheEvict(value = "secrets", key = "#name")
    public Secret updateSecret(String name, String newValue, Long userId, String ipAddress) throws Exception {
        Secret current = secretRepository.findByNameAndActiveTrue(name)
                .orElseThrow(() -> new RuntimeException("Secret not found"));

        // mark current version inactive
        current.setActive(false);
        secretRepository.save(current);

        // create new version
        Secret newSecret = new Secret();
        newSecret.setName(current.getName());
        newSecret.setProjectId(current.getProjectId());
        newSecret.setCreatedBy(userId);
        newSecret.setVersion(current.getVersion() + 1);
        newSecret.setActive(true);
        newSecret.setEncryptedValue(aesEncryptionService.encrypt(newValue));

        auditService.publishEvent(userId, "UPDATE", name, ipAddress);
        return secretRepository.save(newSecret);
    }

    @CacheEvict(value = "secrets", key = "#name")
    public void deleteSecret(String name, Long userId, String ipAddress) {
        Secret secret = secretRepository.findByNameAndActiveTrue(name)
                .orElseThrow(() -> new RuntimeException("Secret not found"));
        auditService.publishEvent(userId, "DELETE", name, ipAddress);
        secretRepository.delete(secret);
    }

    public List<Secret> getSecretHistory(String name) {
        return secretRepository.findByNameOrderByVersionDesc(name);
    }

    public String getSecretByVersion(String name, Integer version) throws Exception {
        Secret secret = secretRepository.findByNameAndVersion(name, version)
                .orElseThrow(() -> new RuntimeException("Version not found"));
        return aesEncryptionService.decrypt(secret.getEncryptedValue());
    }

    public Page<Secret> getAllSecrets(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return secretRepository.findAll(pageable);
    }
}