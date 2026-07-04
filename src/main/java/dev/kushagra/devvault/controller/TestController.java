package dev.kushagra.devvault.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminOnly() {
        return "Hello ADMIN!";
    }

    @GetMapping("/member")
    @PreAuthorize("hasRole('MEMBER')")
    public String memberOnly() {
        return "Hello MEMBER!";
    }

    @GetMapping("/public")
    public String publicEndpoint() {
        return "Hello everyone!";
    }
}