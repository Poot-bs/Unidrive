package com.covoiturage.dto;

public class VehiculeCreateRequest {
    private String chauffeurId;
    private String marque;
    private String modele;
    private String immatriculation;
    private int capacite;

    public String getChauffeurId() {
        return chauffeurId;
    }

    public void setChauffeurId(String chauffeurId) {
        this.chauffeurId = chauffeurId;
    }

    public String getMarque() {
        return marque;
    }

    public void setMarque(String marque) {
        this.marque = marque;
    }

    public String getModele() {
        return modele;
    }

    public void setModele(String modele) {
        this.modele = modele;
    }

    public String getImmatriculation() {
        return immatriculation;
    }

    public void setImmatriculation(String immatriculation) {
        this.immatriculation = immatriculation;
    }

    public int getCapacite() {
        return capacite;
    }

    public void setCapacite(int capacite) {
        this.capacite = capacite;
    }
}
