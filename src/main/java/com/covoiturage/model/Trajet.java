package com.covoiturage.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Trajet implements Serializable {
    private final String id;
    private final Chauffeur chauffeur;
    private final Vehicule vehicule;
    private final String depart;
    private final String arrivee;
    private final LocalDateTime dateDepart;
    private final int placesMax;
    private final double prixParPlace;
    private TripStatus etat;
    private final List<Reservation> reservations;

    public Trajet(String id, Chauffeur chauffeur, Vehicule vehicule, String depart, String arrivee,
                  LocalDateTime dateDepart, int placesMax, double prixParPlace) {
        this.id = id;
        this.chauffeur = Objects.requireNonNull(chauffeur, "chauffeur obligatoire");
        this.vehicule = Objects.requireNonNull(vehicule, "vehicule obligatoire");
        this.depart = Objects.requireNonNull(depart, "depart obligatoire");
        this.arrivee = Objects.requireNonNull(arrivee, "arrivee obligatoire");
        this.dateDepart = dateDepart;
        this.placesMax = placesMax;
        this.prixParPlace = prixParPlace;
        this.etat = TripStatus.OPEN;
        this.reservations = new ArrayList<>();
    }

    public void ajouterPassager(Reservation reservation) {
        if (this.etat == TripStatus.CANCELED || this.etat == TripStatus.CLOSED) {
            throw new IllegalStateException("Ce trajet ne peut plus recevoir de reservation");
        }
        if (getPlacesDisponibles() <= 0) {
            throw new IllegalStateException("Plus de place disponible");
        }
        this.reservations.add(reservation);
        mettreAJourEtatAutomatique();
    }

    public void retirerPassager(Reservation reservation) {
        this.reservations.remove(reservation);
        mettreAJourEtatAutomatique();
    }

    public void cloreTrajet() {
        this.etat = TripStatus.CLOSED;
    }

    public boolean chauffeurPeutAnnuler() {
        return reservations.isEmpty();
    }

    public int getPlacesReservees() {
        return (int) reservations.stream()
            .filter(r -> r.getReservationStatus() == ReservationStatus.PENDING || r.getReservationStatus() == ReservationStatus.ACCEPTED)
            .count();
    }

    public int getPlacesDisponibles() {
        return placesMax - getPlacesReservees();
    }

    public void annulerTrajet() {
        this.etat = TripStatus.CANCELED;
    }

    private void mettreAJourEtatAutomatique() {
        if (etat == TripStatus.CLOSED || etat == TripStatus.CANCELED) {
            return;
        }
        if (getPlacesDisponibles() <= 0) {
            etat = TripStatus.COMPLETE;
        } else {
            etat = TripStatus.OPEN;
        }
    }

    public List<Reservation> getReservations() {
        return Collections.unmodifiableList(new ArrayList<>(reservations));
    }

    public String getId() {
        return id;
    }

    public Chauffeur getChauffeur() {
        return chauffeur;
    }

    public Vehicule getVehicule() {
        return vehicule;
    }

    public String getDepart() {
        return depart;
    }

    public String getArrivee() {
        return arrivee;
    }

    public LocalDateTime getDateDepart() {
        return dateDepart;
    }

    public int getPlacesMax() {
        return placesMax;
    }

    public double getPrixParPlace() {
        return prixParPlace;
    }

    public TripStatus getEtat() {
        return etat;
    }

    public void setEtat(TripStatus etat) {
        this.etat = etat;
    }

    @Override
    public String toString() {
        return "Trajet{" +
            "id='" + id + '\'' +
            ", chauffeur=" + chauffeur.getIdentifiant() +
            ", depart='" + depart + '\'' +
            ", arrivee='" + arrivee + '\'' +
            ", dateDepart=" + dateDepart +
            ", placesMax=" + placesMax +
            ", prixParPlace=" + prixParPlace +
            ", etat=" + etat +
            '}';
    }
}