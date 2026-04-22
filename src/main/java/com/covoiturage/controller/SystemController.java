package com.covoiturage.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/system")
public class SystemController {
    @Value("${spring.application.name:uniride}")
    private String appName;

    @Value("${app.persistence.mode:memory}")
    private String persistenceMode;

    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of(
            "application", appName,
            "status", "UP",
            "persistenceMode", persistenceMode,
            "timestamp", LocalDateTime.now().toString()
        );
    }
}
