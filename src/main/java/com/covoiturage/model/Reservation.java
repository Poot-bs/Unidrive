package com.covoiturage.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Reservation implements Serializable {
    private final String id;
    private final Trajet trajet;
    private final Passager passager;
    private final MoyenPaiement moyenPaiement;
    private final LocalDateTime dateReservation;
    private final double montant;
    private ReservationStatus reservationStatus;
    private PaymentStatus paymentStatus;

    public Reservation(String id, Trajet trajet, Passager passager, MoyenPaiement moyenPaiement, double montant) {
        this.id = id;
        this.trajet = Objects.requireNonNull(trajet, "trajet obligatoire");
        this.passager = Objects.requireNonNull(passager, "passager obligatoire");
        this.moyenPaiement = Objects.requireNonNull(moyenPaiement, "moyen de paiement obligatoire");
        this.montant = montant;
        this.reservationStatus = ReservationStatus.PENDING;
        this.paymentStatus = PaymentStatus.AUTHORIZED;
        this.dateReservation = LocalDateTime.now();
    }

    public void confirmerReservation() {
        this.reservationStatus = ReservationStatus.ACCEPTED;
        this.paymentStatus = PaymentStatus.CAPTURED;
    }

    public double annulerReservationParPassager(LocalDateTime now) {
        if (now.plusHours(24).isBefore(trajet.getDateDepart())) {
            reservationStatus = ReservationStatus.REFUNDED_FULL;
            paymentStatus = PaymentStatus.REFUNDED_FULL;
            return montant;
        }
        reservationStatus = ReservationStatus.REFUNDED_PARTIAL;
        paymentStatus = PaymentStatus.REFUNDED_PARTIAL;
        return montant * 0.5;
    }

    public double annulerReservationParChauffeur(LocalDateTime now) {
        reservationStatus = ReservationStatus.PENALIZED;
        if (now.plusHours(24).isAfter(trajet.getDateDepart())) {
            double penalty = montant * 0.20;
            paymentStatus = PaymentStatus.REFUNDED_PARTIAL;
            return montant + penalty;
        } else {
            paymentStatus = PaymentStatus.REFUNDED_FULL;
            return montant;
        }
    }

    public String getId() {
        return id;
    }

    public Trajet getTrajet() {
        return trajet;
    }

    public Passager getPassager() {
        return passager;
    }

    public MoyenPaiement getMoyenPaiement() {
        return moyenPaiement;
    }

    public ReservationStatus getReservationStatus() {
        return reservationStatus;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public LocalDateTime getDateReservation() {
        return dateReservation;
    }

    public double getMontant() {
        return montant;
    }

    @Override
    public String toString() {
        return "Reservation{" +
            "id='" + id + '\'' +
            ", trajet=" + trajet.getId() +
            ", passager=" + passager.getIdentifiant() +
            ", montant=" + montant +
            ", reservationStatus=" + reservationStatus +
            ", paymentStatus=" + paymentStatus +
            '}';
    }
}