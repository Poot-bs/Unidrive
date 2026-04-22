package com.covoiturage.repository;

import com.covoiturage.model.Reservation;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository {
    Reservation save(Reservation reservation);
    Optional<Reservation> findById(String id);
    List<Reservation> findAll();
    List<Reservation> findByTrajetId(String trajetId);
    List<Reservation> findByPassagerId(String passagerId);
}
