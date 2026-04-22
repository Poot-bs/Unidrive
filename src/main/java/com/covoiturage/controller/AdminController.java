package com.covoiturage.controller;

import com.covoiturage.model.Trajet;
import com.covoiturage.model.User;
import com.covoiturage.service.AdminService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/users/{id}/suspend")
    public User suspendreCompte(@PathVariable String id) {
        return adminService.suspendreCompte(id);
    }

    @PostMapping("/users/{id}/block")
    public User bloquerUtilisateur(@PathVariable String id) {
        return adminService.bloquerUtilisateur(id);
    }

    @GetMapping("/users")
    public List<User> getUsers() {
        return adminService.getUtilisateurs();
    }

    @GetMapping("/trips")
    public List<Trajet> getTrips() {
        return adminService.superviserTrajets();
    }

    @GetMapping("/dashboard")
    public Map<String, Object> dashboard() {
        return adminService.dashboard();
    }
}
