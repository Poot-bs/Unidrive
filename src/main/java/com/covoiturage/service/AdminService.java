package com.covoiturage.service;

import com.covoiturage.model.Trajet;
import com.covoiturage.model.User;
import com.covoiturage.model.UserRole;
import com.covoiturage.model.UserStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AdminService {
    private final AuthService authService;
    private final TrajetService trajetService;

    public AdminService(AuthService authService, TrajetService trajetService) {
        this.authService = authService;
        this.trajetService = trajetService;
    }

    public User suspendreCompte(String userId) {
        return authService.suspendreCompte(userId);
    }

    public User bloquerUtilisateur(String userId) {
        return authService.bloquerUtilisateur(userId);
    }

    public List<User> getUtilisateurs() {
        return authService.getUsers();
    }

    public List<Trajet> superviserTrajets() {
        return trajetService.getTousTrajets();
    }

    public Map<String, Object> dashboard() {
        List<User> users = authService.getUsers();
        List<Trajet> trips = trajetService.getTousTrajets();

        return Map.of(
            "users", users.size(),
            "trips", trips.size(),
            "admins", users.stream().filter(u -> u.getRole() == UserRole.ADMIN).count(),
            "chauffeurs", users.stream().filter(u -> u.getRole() == UserRole.CHAUFFEUR).count(),
            "passagers", users.stream().filter(u -> u.getRole() == UserRole.PASSAGER).count(),
            "activeUsers", users.stream().filter(u -> u.getStatus() == UserStatus.ACTIVE).count(),
            "suspendedUsers", users.stream().filter(u -> u.getStatus() == UserStatus.SUSPENDED).count(),
            "blockedUsers", users.stream().filter(u -> u.getStatus() == UserStatus.BLOCKED).count()
        );
    }
}
