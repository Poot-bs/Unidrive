package com.covoiturage.service;

import com.covoiturage.model.PaymentStatus;
import com.covoiturage.model.Reservation;
import org.springframework.stereotype.Service;

@Service
public class PaiementService {

    public PaymentStatus payer(Reservation reservation) {
        return reservation.getPaymentStatus();
    }

    public PaymentStatus capturer(Reservation reservation) {
        reservation.confirmerReservation();
        return reservation.getPaymentStatus();
    }

    public double rembourser(Reservation reservation, double montant) {
        return montant;
    }
}
