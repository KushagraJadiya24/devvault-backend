package dev.kushagra.devvault.controller;

import dev.kushagra.devvault.dto.SecretRequest;
import dev.kushagra.devvault.model.Secret;
import dev.kushagra.devvault.service.SecretService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/secrets")
@RequiredArgsConstructor
public class SecretController {

    private final SecretService secretService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Secret createSecret(@Valid @RequestBody SecretRequest request,
                               HttpServletRequest httpRequest) throws Exception {
        Long userId = (Long) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        String ipAddress = httpRequest.getRemoteAddr();
        return secretService.createSecret(request, userId, ipAddress);
    }

    @GetMapping("/{name}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MEMBER')")
    public String getSecret(@PathVariable String name,
                            HttpServletRequest httpRequest) throws Exception {
        Long userId = (Long) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        String ipAddress = httpRequest.getRemoteAddr();
        return secretService.getSecretByName(name, userId, ipAddress);
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
    public Secret updateSecret(@PathVariable String name,
                               @RequestBody String newValue,
                               HttpServletRequest httpRequest) throws Exception {
        Long userId = (Long) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        String ipAddress = httpRequest.getRemoteAddr();
        return secretService.updateSecret(name, newValue, userId, ipAddress);
    }

    @DeleteMapping("/{name}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteSecret(@PathVariable String name,
                                             HttpServletRequest httpRequest) {
        Long userId = (Long) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        String ipAddress = httpRequest.getRemoteAddr();
        secretService.deleteSecret(name, userId, ipAddress);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/{name}/history")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MEMBER')")
    public List<Secret> getSecretHistory(@PathVariable String name) {
        return secretService.getSecretHistory(name);
    }

    @GetMapping("/{name}/version/{version}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MEMBER')")
    public String getSecretByVersion(@PathVariable String name,
                                     @PathVariable Integer version) throws Exception {
        return secretService.getSecretByVersion(name, version);
    }
}