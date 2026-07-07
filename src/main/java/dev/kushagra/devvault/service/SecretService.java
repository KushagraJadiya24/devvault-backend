package dev.kushagra.devvault.service;

import dev.kushagra.devvault.dto.SecretRequest;
import dev.kushagra.devvault.model.Secret;
import dev.kushagra.devvault.repository.SecretRepository;
import lombok.RequiredArgsConstructor;
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

    public String getSecretByName(String name) throws Exception {
        Secret secret = secretRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Secret not found"));

        return aesEncryptionService.decrypt(secret.getEncryptedValue());
    }
}