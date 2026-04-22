package com.covoiturage.controller;

import com.covoiturage.dto.ReservationCreateRequest;
import com.covoiturage.model.Reservation;
import com.covoiturage.service.ReservationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {
    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    public Reservation creerReservation(@RequestBody ReservationCreateRequest request) {
        return reservationService.creerReservation(request);
    }

    @PostMapping("/{id}/confirm")
    public Reservation confirmerReservation(@PathVariable String id, @RequestParam String chauffeurId) {
        return reservationService.confirmerReservation(id, chauffeurId);
    }

    @PostMapping("/{id}/cancel")
    public Reservation annulerReservation(
        @PathVariable String id,
        @RequestParam String initiateurId,
        @RequestParam boolean initiateurChauffeur
    ) {
        return reservationService.annulerReservation(id, initiateurId, initiateurChauffeur);
    }

    @GetMapping
    public List<Reservation> getReservations() {
        return reservationService.getReservations();
    }

    @GetMapping("/passager/{passagerId}")
    public List<Reservation> getReservationsPassager(@PathVariable String passagerId) {
        return reservationService.getReservationsParPassager(passagerId);
    }

    @GetMapping("/passager/{passagerId}/suivi")
    public List<Map<String, Object>> getSuiviReservationsPassager(@PathVariable String passagerId) {
        return reservationService.getReservationsParPassager(passagerId).stream().map(r -> {
            var trajet = r.getTrajet();
            var chauffeur = trajet.getChauffeur();
            var canContact = r.getReservationStatus().name().equals("ACCEPTED");
            Map<String, Object> payload = new HashMap<>();
            payload.put("reservationId", r.getId());
            payload.put("reservationStatus", r.getReservationStatus().name());
            payload.put("paymentStatus", r.getPaymentStatus().name());
            payload.put("tripId", trajet.getId());
            payload.put("depart", trajet.getDepart());
            payload.put("arrivee", trajet.getArrivee());
            payload.put("dateDepart", String.valueOf(trajet.getDateDepart()));
            payload.put("prixParPlace", trajet.getPrixParPlace());
            payload.put("chauffeurId", chauffeur.getIdentifiant());
            payload.put("chauffeurNom", chauffeur.getNom());
            payload.put("chauffeurEmail", chauffeur.getEmail());
            payload.put("canContactChauffeur", canContact);
            return payload;
        }).toList();
    }

    @GetMapping("/chauffeur/{chauffeurId}/demandes")
    public List<Map<String, Object>> getDemandesChauffeur(@PathVariable String chauffeurId) {
        return reservationService.getReservationsParChauffeur(chauffeurId).stream().map(r -> {
            var trajet = r.getTrajet();
            var passager = r.getPassager();
            Map<String, Object> payload = new HashMap<>();
            payload.put("reservationId", r.getId());
            payload.put("reservationStatus", r.getReservationStatus().name());
            payload.put("paymentStatus", r.getPaymentStatus().name());
            payload.put("tripId", trajet.getId());
            payload.put("depart", trajet.getDepart());
            payload.put("arrivee", trajet.getArrivee());
            payload.put("dateDepart", String.valueOf(trajet.getDateDepart()));
            payload.put("prixParPlace", trajet.getPrixParPlace());
            payload.put("passagerId", passager.getIdentifiant());
            payload.put("passagerNom", passager.getNom());
            payload.put("passagerEmail", passager.getEmail());
            return payload;
        }).toList();
    }
}
