package dev.kushagra.devvault.controller;

import dev.kushagra.devvault.dto.SecretRequest;
import dev.kushagra.devvault.model.Environment;
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

    @GetMapping("/project/{projectId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MEMBER')")
    public List<Secret> getSecretsByProject(@PathVariable Long projectId) {
        return secretService.getSecretsByProject(projectId);
    }

    @GetMapping("/project/{projectId}/env/{environment}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MEMBER')")
    public List<Secret> getSecretsByEnvironment(@PathVariable Long projectId,
                                                @PathVariable Environment environment) {
        return secretService.getSecretsByProjectAndEnvironment(projectId, environment);
    }

    @GetMapping("/project/{projectId}/{name}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MEMBER')")
    public String getSecret(@PathVariable Long projectId,
                            @PathVariable String name,
                            HttpServletRequest httpRequest) throws Exception {
        Long userId = (Long) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        String ipAddress = httpRequest.getRemoteAddr();
        return secretService.getSecretByName(name, projectId, userId, ipAddress);
    }

    @PutMapping("/project/{projectId}/{name}")
    @PreAuthorize("hasRole('ADMIN')")
    public Secret updateSecret(@PathVariable Long projectId,
                               @PathVariable String name,
                               @RequestBody String newValue,
                               HttpServletRequest httpRequest) throws Exception {
        Long userId = (Long) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        String ipAddress = httpRequest.getRemoteAddr();
        return secretService.updateSecret(name, projectId, newValue, userId, ipAddress);
    }

    @DeleteMapping("/project/{projectId}/{name}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteSecret(@PathVariable Long projectId,
                                             @PathVariable String name,
                                             HttpServletRequest httpRequest) {
        Long userId = (Long) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        String ipAddress = httpRequest.getRemoteAddr();
        secretService.deleteSecret(name, projectId, userId, ipAddress);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/project/{projectId}/{name}/history")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MEMBER')")
    public List<Secret> getSecretHistory(@PathVariable Long projectId,
                                         @PathVariable String name) {
        return secretService.getSecretHistory(name, projectId);
    }

    @GetMapping("/project/{projectId}/{name}/version/{version}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MEMBER')")
    public String getSecretByVersion(@PathVariable Long projectId,
                                     @PathVariable String name,
                                     @PathVariable Integer version) throws Exception {
        return secretService.getSecretByVersion(name, projectId, version);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MEMBER')")
    public Page<Secret> getAllSecrets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return secretService.getAllSecrets(page, size);
    }
}