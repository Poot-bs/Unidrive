package com.covoiturage.repository;

import com.covoiturage.model.Reservation;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@ConditionalOnProperty(name = "app.persistence.mode", havingValue = "memory", matchIfMissing = true)
public class InMemoryReservationRepository implements ReservationRepository {
    private final Map<String, Reservation> reservations = new ConcurrentHashMap<>();

    @Override
    public Reservation save(Reservation reservation) {
        reservations.put(reservation.getId(), reservation);
        return reservation;
    }

    @Override
    public Optional<Reservation> findById(String id) {
        return Optional.ofNullable(reservations.get(id));
    }

    @Override
    public List<Reservation> findAll() {
        return new ArrayList<>(reservations.values());
    }

    @Override
    public List<Reservation> findByTrajetId(String trajetId) {
        return reservations.values().stream().filter(r -> r.getTrajet().getId().equals(trajetId)).toList();
    }

    @Override
    public List<Reservation> findByPassagerId(String passagerId) {
        return reservations.values().stream().filter(r -> r.getPassager().getIdentifiant().equals(passagerId)).toList();
    }
}
