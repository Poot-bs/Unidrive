package com.covoiturage.repository.supabase;

import com.covoiturage.model.Trajet;
import com.covoiturage.repository.TrajetRepository;
import com.covoiturage.repository.serialization.JavaSerializationUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@ConditionalOnProperty(name = "app.persistence.mode", havingValue = "supabase")
public class SupabaseTrajetRepository implements TrajetRepository {
    private static final String TABLE = "trajets_store";

    private final SupabaseRestClient supabaseRestClient;

    public SupabaseTrajetRepository(SupabaseRestClient supabaseRestClient) {
        this.supabaseRestClient = supabaseRestClient;
    }

    @Override
    public Trajet save(Trajet trajet) {
        String payload = JavaSerializationUtils.toBase64(trajet);
        supabaseRestClient.upsert(TABLE, Map.of(
            "id", trajet.getId(),
            "payload", payload
        ), "id");
        return trajet;
    }

    @Override
    public Optional<Trajet> findById(String id) {
        List<Map<String, Object>> rows = supabaseRestClient.select(TABLE, Map.of("id", "eq." + id), "payload", 1);
        return rows.stream()
            .findFirst()
            .map(r -> JavaSerializationUtils.fromBase64(String.valueOf(r.get("payload")), Trajet.class));
    }

    @Override
    public List<Trajet> findAll() {
        List<Map<String, Object>> rows = supabaseRestClient.select(TABLE, Map.of(), "payload", null);
        return rows.stream()
            .map(r -> JavaSerializationUtils.fromBase64(String.valueOf(r.get("payload")), Trajet.class))
            .toList();
    }
}
