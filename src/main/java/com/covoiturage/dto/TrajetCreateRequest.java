package com.covoiturage.dto;

import java.time.LocalDateTime;

public class TrajetCreateRequest {
    private String chauffeurId;
    private String vehiculeId;
    private String depart;
    private String arrivee;
    private LocalDateTime dateDepart;
    private int placesMax;
    private double prixParPlace;

    public String getChauffeurId() {
        return chauffeurId;
    }

    public void setChauffeurId(String chauffeurId) {
        this.chauffeurId = chauffeurId;
    }

    public String getVehiculeId() {
        return vehiculeId;
    }

    public void setVehiculeId(String vehiculeId) {
        this.vehiculeId = vehiculeId;
    }

    public String getDepart() {
        return depart;
    }

    public void setDepart(String depart) {
        this.depart = depart;
    }

    public String getArrivee() {
        return arrivee;
    }

    public void setArrivee(String arrivee) {
        this.arrivee = arrivee;
    }

    public LocalDateTime getDateDepart() {
        return dateDepart;
    }

    public void setDateDepart(LocalDateTime dateDepart) {
        this.dateDepart = dateDepart;
    }

    public int getPlacesMax() {
        return placesMax;
    }

    public void setPlacesMax(int placesMax) {
        this.placesMax = placesMax;
    }

    public double getPrixParPlace() {
        return prixParPlace;
    }

    public void setPrixParPlace(double prixParPlace) {
        this.prixParPlace = prixParPlace;
    }
}
