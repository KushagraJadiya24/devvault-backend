package dev.kushagra.devvault.controller;

import dev.kushagra.devvault.dto.SecretRequest;
import dev.kushagra.devvault.service.SecretService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/secrets")
@RequiredArgsConstructor
public class SecretController {
    private final SecretService secretService;

    @PostMapping("/api/secrets")
    @PreAuthorize("hasRole('ADMIN')")
    public String createSecret(@Valid @RequestBody SecretRequest request){
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String Secret = secretService.createSecret(request,userId);

        return Secret;
    }
}
