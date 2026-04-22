package com.covoiturage.dto;

public class MoyenPaiementCreateRequest {
    private String passagerId;
    private String holderName;
    private String type;
    private String cardLast4;

    public String getPassagerId() {
        return passagerId;
    }

    public void setPassagerId(String passagerId) {
        this.passagerId = passagerId;
    }

    public String getHolderName() {
        return holderName;
    }

    public void setHolderName(String holderName) {
        this.holderName = holderName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCardLast4() {
        return cardLast4;
    }

    public void setCardLast4(String cardLast4) {
        this.cardLast4 = cardLast4;
    }
}
