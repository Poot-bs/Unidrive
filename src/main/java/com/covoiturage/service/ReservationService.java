package com.covoiturage.service;

import com.covoiturage.dto.ReservationCreateRequest;
import com.covoiturage.exception.InvalidStateException;
import com.covoiturage.exception.NotFoundException;
import com.covoiturage.exception.ValidationException;
import com.covoiturage.model.Chauffeur;
import com.covoiturage.model.MoyenPaiement;
import com.covoiturage.model.Passager;
import com.covoiturage.model.Reservation;
import com.covoiturage.model.ReservationStatus;
import com.covoiturage.model.User;
import com.covoiturage.repository.ReservationRepository;
import com.covoiturage.repository.TrajetRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final TrajetRepository trajetRepository;
    private final AuthService authService;
    private final PaiementService paiementService;
    private final NotificationService notificationService;

    public ReservationService(ReservationRepository reservationRepository,
                              TrajetRepository trajetRepository,
                              AuthService authService,
                              PaiementService paiementService,
                              NotificationService notificationService) {
        this.reservationRepository = reservationRepository;
        this.trajetRepository = trajetRepository;
        this.authService = authService;
        this.paiementService = paiementService;
        this.notificationService = notificationService;
    }

    public Reservation creerReservation(ReservationCreateRequest request) {
        if (request == null || isBlank(request.getPassagerId()) || isBlank(request.getTrajetId()) || isBlank(request.getMoyenPaiementId())) {
            throw new ValidationException("passagerId, trajetId et moyenPaiementId sont obligatoires");
        }

        User user = authService.getUtilisateur(request.getPassagerId());
        if (!(user instanceof Passager passager)) {
            throw new ValidationException("Seul un passager peut reserver");
        }

        var trajet = trajetRepository.findById(request.getTrajetId())
            .orElseThrow(() -> new NotFoundException("Trajet introuvable"));

        MoyenPaiement moyenPaiement = passager.getMoyensPaiement().stream()
            .filter(m -> m.getId().equals(request.getMoyenPaiementId()))
            .findFirst()
            .orElseThrow(() -> new NotFoundException("Moyen de paiement introuvable"));

        boolean activeReservationExists = trajet.getReservations().stream()
            .anyMatch(r -> r.getPassager().equals(passager)
                && (r.getReservationStatus() == ReservationStatus.PENDING || r.getReservationStatus() == ReservationStatus.ACCEPTED));
        if (activeReservationExists) {
            throw new ValidationException("Vous avez deja une reservation active sur ce trajet");
        }

        Reservation reservation = new Reservation(
            UUID.randomUUID().toString(),
            trajet,
            passager,
            moyenPaiement,
            trajet.getPrixParPlace()
        );

        paiementService.payer(reservation);
        trajet.ajouterPassager(reservation);
        passager.ajouterReservation(reservation);

        reservationRepository.save(reservation);
        trajetRepository.save(trajet);

        notificationService.notifierEmail(
            trajet.getChauffeur(),
            "Nouvelle demande de reservation de " + passager.getNom() + " pour le trajet " + trajet.getDepart() + " -> " + trajet.getArrivee()
        );
        notificationService.notifierSMS(
            trajet.getChauffeur(),
            "Nouvelle demande en attente. Consultez vos demandes chauffeur."
        );
        notificationService.notifierEmail(passager, "Reservation creee et paiement autorise");
        notificationService.notifierSMS(passager, "Reservation en attente de confirmation du chauffeur");
        return reservation;
    }

    public Reservation confirmerReservation(String reservationId, String chauffeurId) {
        Reservation reservation = getReservation(reservationId);
        User user = authService.getUtilisateur(chauffeurId);
        if (!(user instanceof Chauffeur chauffeur)) {
            throw new ValidationException("Seul un chauffeur peut confirmer une reservation");
        }
        if (!reservation.getTrajet().getChauffeur().equals(chauffeur)) {
            throw new ValidationException("Ce chauffeur ne possede pas ce trajet");
        }
        paiementService.capturer(reservation);
        reservationRepository.save(reservation);
        notificationService.notifierEmail(reservation.getPassager(), "Reservation acceptee et paiement capture");
        notificationService.notifierSMS(reservation.getPassager(), "Votre reservation a ete acceptee par le chauffeur.");
        notificationService.notifierEmail(chauffeur, "Vous avez accepte la reservation " + reservation.getId());
        return reservation;
    }

    public Reservation annulerReservation(String reservationId, String initiateurId, boolean initiateurChauffeur) {
        Reservation reservation = getReservation(reservationId);
        double remboursement;

        if (initiateurChauffeur) {
            if (!reservation.getTrajet().getChauffeur().getIdentifiant().equals(initiateurId)) {
                throw new InvalidStateException("Le chauffeur n'est pas autorise pour cette reservation");
            }
            remboursement = reservation.annulerReservationParChauffeur(LocalDateTime.now());
            notificationService.notifierEmail(reservation.getPassager(), "Reservation annulee par le chauffeur. Remboursement: " + remboursement);
            notificationService.notifierSMS(reservation.getPassager(), "Votre reservation a ete refusee/annulee par le chauffeur.");
            notificationService.notifierEmail(reservation.getTrajet().getChauffeur(), "Vous avez refuse la reservation " + reservation.getId());
        } else {
            if (!reservation.getPassager().getIdentifiant().equals(initiateurId)) {
                throw new InvalidStateException("Le passager n'est pas autorise pour cette reservation");
            }
            remboursement = reservation.annulerReservationParPassager(LocalDateTime.now());
            notificationService.notifierEmail(reservation.getPassager(), "Reservation annulee. Remboursement: " + remboursement);
        }

        reservation.getTrajet().retirerPassager(reservation);
        reservation.getPassager().retirerReservation(reservation);

        paiementService.rembourser(reservation, remboursement);
        reservationRepository.save(reservation);
        trajetRepository.save(reservation.getTrajet());
        return reservation;
    }

    public List<Reservation> getReservations() {
        return reservationRepository.findAll();
    }

    public List<Reservation> getReservationsParPassager(String passagerId) {
        return reservationRepository.findByPassagerId(passagerId);
    }

    public List<Reservation> getReservationsParChauffeur(String chauffeurId) {
        return reservationRepository.findAll().stream()
            .filter(r -> r.getTrajet().getChauffeur().getIdentifiant().equals(chauffeurId))
            .toList();
    }

    public Reservation getReservation(String id) {
        return reservationRepository.findById(id).orElseThrow(() -> new NotFoundException("Reservation introuvable"));
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
