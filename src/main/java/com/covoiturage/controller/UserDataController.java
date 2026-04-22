package com.covoiturage.controller;

import com.covoiturage.dto.MoyenPaiementCreateRequest;
import com.covoiturage.dto.VehiculeCreateRequest;
import com.covoiturage.model.MoyenPaiement;
import com.covoiturage.model.Notification;
import com.covoiturage.model.Vehicule;
import com.covoiturage.service.AuthService;
import com.covoiturage.service.NotificationService;
import com.covoiturage.service.UserDataService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserDataController {
    private final UserDataService userDataService;
    private final AuthService authService;
    private final NotificationService notificationService;

    public UserDataController(UserDataService userDataService, AuthService authService, NotificationService notificationService) {
        this.userDataService = userDataService;
        this.authService = authService;
        this.notificationService = notificationService;
    }

    @PostMapping("/vehicles")
    public Vehicule addVehicule(@RequestBody VehiculeCreateRequest request) {
        return userDataService.addVehicule(request);
    }

    @GetMapping("/{chauffeurId}/vehicles")
    public List<Vehicule> getVehicules(@PathVariable String chauffeurId) {
        return userDataService.getVehicules(chauffeurId);
    }

    @PostMapping("/payment-methods")
    public MoyenPaiement addMoyenPaiement(@RequestBody MoyenPaiementCreateRequest request) {
        return userDataService.addMoyenPaiement(request);
    }

    @GetMapping("/{passagerId}/payment-methods")
    public List<MoyenPaiement> getMoyensPaiement(@PathVariable String passagerId) {
        return userDataService.getMoyensPaiement(passagerId);
    }

    @PostMapping("/rate-driver")
    public void evaluerChauffeur(@RequestParam String passagerId, @RequestParam String chauffeurId, @RequestParam int note) {
        userDataService.evaluerChauffeur(passagerId, chauffeurId, note);
    }

    @GetMapping("/{chauffeurId}/rating")
    public double consulterNotes(@PathVariable String chauffeurId) {
        return userDataService.consulterNotesChauffeur(chauffeurId);
    }

    @GetMapping("/{userId}/notifications")
    public List<Notification> getNotifications(@PathVariable String userId) {
        return notificationService.getNotifications(authService.getUtilisateur(userId));
    }
}
