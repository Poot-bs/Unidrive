package com.covoiturage.dto;

public class ReservationCreateRequest {
    private String trajetId;
    private String passagerId;
    private String moyenPaiementId;

    public String getTrajetId() {
        return trajetId;
    }

    public void setTrajetId(String trajetId) {
        this.trajetId = trajetId;
    }

    public String getPassagerId() {
        return passagerId;
    }

    public void setPassagerId(String passagerId) {
        this.passagerId = passagerId;
    }

    public String getMoyenPaiementId() {
        return moyenPaiementId;
    }

    public void setMoyenPaiementId(String moyenPaiementId) {
        this.moyenPaiementId = moyenPaiementId;
    }
}
