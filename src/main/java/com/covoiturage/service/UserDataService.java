package com.covoiturage.service;

import com.covoiturage.dto.MoyenPaiementCreateRequest;
import com.covoiturage.dto.VehiculeCreateRequest;
import com.covoiturage.exception.ValidationException;
import com.covoiturage.model.Chauffeur;
import com.covoiturage.model.MoyenPaiement;
import com.covoiturage.model.Passager;
import com.covoiturage.model.User;
import com.covoiturage.model.Vehicule;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserDataService {
    private final AuthService authService;

    public UserDataService(AuthService authService) {
        this.authService = authService;
    }

    public Vehicule addVehicule(VehiculeCreateRequest request) {
        User user = authService.getUtilisateur(request.getChauffeurId());
        if (!(user instanceof Chauffeur chauffeur)) {
            throw new ValidationException("Seul un chauffeur peut ajouter un vehicule");
        }
        Vehicule vehicule = new Vehicule(
            UUID.randomUUID().toString(),
            request.getMarque(),
            request.getModele(),
            request.getImmatriculation(),
            request.getCapacite()
        );
        chauffeur.ajouterVehicule(vehicule);
        return vehicule;
    }

    public List<Vehicule> getVehicules(String chauffeurId) {
        User user = authService.getUtilisateur(chauffeurId);
        if (!(user instanceof Chauffeur chauffeur)) {
            throw new ValidationException("Utilisateur non chauffeur");
        }
        return chauffeur.getVehicules();
    }

    public MoyenPaiement addMoyenPaiement(MoyenPaiementCreateRequest request) {
        User user = authService.getUtilisateur(request.getPassagerId());
        if (!(user instanceof Passager passager)) {
            throw new ValidationException("Seul un passager peut ajouter un moyen de paiement");
        }
        MoyenPaiement moyenPaiement = new MoyenPaiement(
            UUID.randomUUID().toString(),
            request.getHolderName(),
            request.getType(),
            request.getCardLast4()
        );
        passager.ajouterMoyenPaiement(moyenPaiement);
        return moyenPaiement;
    }

    public List<MoyenPaiement> getMoyensPaiement(String passagerId) {
        User user = authService.getUtilisateur(passagerId);
        if (!(user instanceof Passager passager)) {
            throw new ValidationException("Utilisateur non passager");
        }
        return passager.getMoyensPaiement();
    }

    public double consulterNotesChauffeur(String chauffeurId) {
        User user = authService.getUtilisateur(chauffeurId);
        if (!(user instanceof Chauffeur chauffeur)) {
            throw new ValidationException("Utilisateur non chauffeur");
        }
        return chauffeur.consulterNotesChauffeur();
    }

    public void evaluerChauffeur(String passagerId, String chauffeurId, int note) {
        User passagerUser = authService.getUtilisateur(passagerId);
        User chauffeurUser = authService.getUtilisateur(chauffeurId);
        if (!(passagerUser instanceof Passager passager) || !(chauffeurUser instanceof Chauffeur chauffeur)) {
            throw new ValidationException("Evaluation invalide");
        }
        try {
            passager.evaluerChauffeur(chauffeur, note);
        } catch (IllegalArgumentException ex) {
            throw new ValidationException(ex.getMessage());
        }
    }
}
