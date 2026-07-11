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

@Service
@RequiredArgsConstructor
public class SecretService {

    private final SecretRepository secretRepository;
    private final AesEncryptionService aesEncryptionService;

    public Secret createSecret(SecretRequest request, Long userId) throws Exception {
        Secret secret = new Secret();
        secret.setName(request.getName());
        secret.setProjectId(request.getProjectId());
        secret.setCreatedBy(userId);
        secret.setVersion(1);
        secret.setEncryptedValue(aesEncryptionService.encrypt(request.getValue()));
        return secretRepository.save(secret);
    }

    @Cacheable(value = "secrets", key = "#name")
    public String getSecretByName(String name) throws Exception {
        Secret secret = secretRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Secret not found"));
        return aesEncryptionService.decrypt(secret.getEncryptedValue());
    }

    @CacheEvict(value = "secrets", key = "#name")
    public Secret updateSecret(String name, String newValue, Long userId) throws Exception {
        Secret secret = secretRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Secret not found"));
        secret.setEncryptedValue(aesEncryptionService.encrypt(newValue));
        secret.setVersion(secret.getVersion() + 1);
        return secretRepository.save(secret);
    }

    @CacheEvict(value = "secrets", key = "#name")
    public void deleteSecret(String name) {
        Secret secret = secretRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Secret not found"));
        secretRepository.delete(secret);
    }

    public Page<Secret> getAllSecrets(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return secretRepository.findAll(pageable);
    }
}