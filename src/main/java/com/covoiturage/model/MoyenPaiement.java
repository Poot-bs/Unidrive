package com.covoiturage.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.io.Serializable;
import java.util.Objects;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class MoyenPaiement implements Serializable {
    private final String id;
    private final String holderName;
    private final String type;
    private final String cardLast4;

    public MoyenPaiement(String id, String holderName, String type, String cardLast4) {
        this.id = id;
        this.holderName = holderName;
        this.type = type;
        this.cardLast4 = cardLast4;
    }

    public String getId() {
        return id;
    }

    public String getHolderName() {
        return holderName;
    }

    public String getType() {
        return type;
    }

    public String getCardLast4() {
        return cardLast4;
    }

    @Override
    public String toString() {
        return "MoyenPaiement{" +
            "id='" + id + '\'' +
            ", holderName='" + holderName + '\'' +
            ", type='" + type + '\'' +
            ", cardLast4='" + cardLast4 + '\'' +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MoyenPaiement that)) {
            return false;
        }
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
