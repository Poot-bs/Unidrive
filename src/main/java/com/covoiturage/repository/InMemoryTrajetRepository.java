package com.covoiturage.repository;

import com.covoiturage.model.Trajet;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@ConditionalOnProperty(name = "app.persistence.mode", havingValue = "memory", matchIfMissing = true)
public class InMemoryTrajetRepository implements TrajetRepository {
    private final Map<String, Trajet> trajets = new ConcurrentHashMap<>();

    @Override
    public Trajet save(Trajet trajet) {
        trajets.put(trajet.getId(), trajet);
        return trajet;
    }

    @Override
    public Optional<Trajet> findById(String id) {
        return Optional.ofNullable(trajets.get(id));
    }

    @Override
    public List<Trajet> findAll() {
        return new ArrayList<>(trajets.values());
    }
}
