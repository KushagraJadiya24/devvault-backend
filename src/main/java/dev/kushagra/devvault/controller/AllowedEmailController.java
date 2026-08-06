package dev.kushagra.devvault.controller;

import dev.kushagra.devvault.model.AllowedEmail;
import dev.kushagra.devvault.repository.AllowedEmailRepository;
import dev.kushagra.devvault.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/allowed-emails")
@RequiredArgsConstructor
public class AllowedEmailController {

    private final AllowedEmailRepository allowedEmailRepository;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<AllowedEmail> getAllowedEmails() {
        return allowedEmailRepository.findAll();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public AllowedEmail addAllowedEmail(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        if (email == null || email.isBlank()) {
            throw new RuntimeException("Email is required");
        }
        if (allowedEmailRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already allowed");
        }
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        AllowedEmail allowedEmail = new AllowedEmail();
        allowedEmail.setEmail(email);
        allowedEmail.setAddedBy(userId);
        return allowedEmailRepository.save(allowedEmail);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> removeAllowedEmail(@PathVariable Long id) {
        allowedEmailRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}