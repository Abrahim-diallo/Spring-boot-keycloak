package fr.brahim.spring_boot_keyclock.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @GetMapping("/hello-admin")
    @PreAuthorize("hasRole('admin_client_role')")
    public String helloAdmin() {
        return "Hello brahim spring boot with ADMIN keyclock";
    }

    @GetMapping("/hello-user")
    @PreAuthorize("hasRole('user_client_role') or hasRole('admin_client_role')")
    public String helloUser() {
        return "Hello brahim spring boot with USER keyclock";
    }
}
