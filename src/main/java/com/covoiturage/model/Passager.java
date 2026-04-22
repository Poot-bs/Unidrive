package com.covoiturage.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Passager extends User {
    private final List<Reservation> reservations;
    private final List<MoyenPaiement> moyensPaiement;

    public Passager(String identifiant, String nom, String email, String password) {
        super(identifiant, nom, email, password, UserRole.PASSAGER);
        this.reservations = new ArrayList<>();
        this.moyensPaiement = new ArrayList<>();
    }

    public void ajouterReservation(Reservation r) {
        Objects.requireNonNull(r, "reservation obligatoire");
        this.reservations.add(r);
    }

    public void retirerReservation(Reservation r) {
        this.reservations.remove(r);
    }

    public void ajouterMoyenPaiement(MoyenPaiement moyenPaiement) {
        Objects.requireNonNull(moyenPaiement, "moyen de paiement obligatoire");
        this.moyensPaiement.add(moyenPaiement);
    }

    public void evaluerChauffeur(Chauffeur chauffeur, int note) {
        if (note < 1 || note > 5) {
            throw new IllegalArgumentException("La note doit etre entre 1 et 5");
        }
        chauffeur.ajouterNote(note);
    }

    public List<Reservation> getReservations() {
        return Collections.unmodifiableList(new ArrayList<>(reservations));
    }

    public List<MoyenPaiement> getMoyensPaiement() {
        return Collections.unmodifiableList(new ArrayList<>(moyensPaiement));
    }
    
    @Override
    public String toString() {
        return "Passager{" +
                "id='" + getIdentifiant() + '\'' +
                ", nom='" + getNom() + '\'' +
                "email='" + getEmail() + '\'' +
                '}';
    }
}