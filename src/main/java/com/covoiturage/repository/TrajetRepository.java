package com.covoiturage.repository;

import com.covoiturage.model.Trajet;

import java.util.List;
import java.util.Optional;

public interface TrajetRepository {
    Trajet save(Trajet trajet);
    Optional<Trajet> findById(String id);
    List<Trajet> findAll();
}
