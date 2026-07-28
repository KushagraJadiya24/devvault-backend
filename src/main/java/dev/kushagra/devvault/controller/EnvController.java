package dev.kushagra.devvault.controller;

import dev.kushagra.devvault.model.Environment;
import dev.kushagra.devvault.model.Secret;
import dev.kushagra.devvault.service.EnvService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/env")
@RequiredArgsConstructor
public class EnvController {

    private final EnvService envService;

    @PostMapping("/import/{projectId}/{environment}")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Secret> importEnv(@PathVariable Long projectId,
                                  @PathVariable Environment environment,
                                  @RequestParam("file") MultipartFile file,
                                  HttpServletRequest httpRequest) throws Exception {
        Long userId = (Long) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        String ipAddress = httpRequest.getRemoteAddr();
        return envService.importEnvFile(file, projectId, environment, userId, ipAddress);
    }

    @GetMapping("/export/{projectId}/{environment}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MEMBER')")
    public ResponseEntity<String> exportEnv(@PathVariable Long projectId,
                                            @PathVariable Environment environment) throws Exception {
        String content = envService.exportEnvFile(projectId, environment);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\".env." + environment.toString().toLowerCase() + "\"")
                .contentType(MediaType.TEXT_PLAIN)
                .body(content);
    }
}