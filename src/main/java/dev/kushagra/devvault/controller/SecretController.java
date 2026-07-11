package dev.kushagra.devvault.controller;

import dev.kushagra.devvault.dto.SecretRequest;
import dev.kushagra.devvault.model.Secret;
import dev.kushagra.devvault.service.SecretService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/secrets")
@RequiredArgsConstructor
public class SecretController {

    private final SecretService secretService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Secret createSecret(@Valid @RequestBody SecretRequest request) throws Exception {
        Long userId = (Long) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return secretService.createSecret(request, userId);
    }

    @GetMapping("/{name}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MEMBER')")
    public String getSecret(@PathVariable String name) throws Exception {
        return secretService.getSecretByName(name);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MEMBER')")
    public Page<Secret> getAllSecrets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return secretService.getAllSecrets(page, size);
    }

    @PutMapping("/{name}")
    @PreAuthorize("hasRole('ADMIN')")
    public Secret updateSecret(
            @PathVariable String name,
            @RequestBody String newValue) throws Exception {
        Long userId = (Long) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return secretService.updateSecret(name, newValue, userId);
    }

    @DeleteMapping("/{name}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteSecret(@PathVariable String name) {
        secretService.deleteSecret(name);
        return ResponseEntity.noContent().build();
    }
}