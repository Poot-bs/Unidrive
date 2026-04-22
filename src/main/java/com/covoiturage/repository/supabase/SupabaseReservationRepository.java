package com.covoiturage.repository.supabase;

import com.covoiturage.model.Reservation;
import com.covoiturage.repository.ReservationRepository;
import com.covoiturage.repository.serialization.JavaSerializationUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@ConditionalOnProperty(name = "app.persistence.mode", havingValue = "supabase")
public class SupabaseReservationRepository implements ReservationRepository {
    private static final String TABLE = "reservations_store";

    private final SupabaseRestClient supabaseRestClient;

    public SupabaseReservationRepository(SupabaseRestClient supabaseRestClient) {
        this.supabaseRestClient = supabaseRestClient;
    }

    @Override
    public Reservation save(Reservation reservation) {
        String payload = JavaSerializationUtils.toBase64(reservation);
        supabaseRestClient.upsert(TABLE, Map.of(
            "id", reservation.getId(),
            "trajet_id", reservation.getTrajet().getId(),
            "passager_id", reservation.getPassager().getIdentifiant(),
            "payload", payload
        ), "id");
        return reservation;
    }

    @Override
    public Optional<Reservation> findById(String id) {
        List<Map<String, Object>> rows = supabaseRestClient.select(TABLE, Map.of("id", "eq." + id), "payload", 1);
        return rows.stream()
            .findFirst()
            .map(r -> JavaSerializationUtils.fromBase64(String.valueOf(r.get("payload")), Reservation.class));
    }

    @Override
    public List<Reservation> findAll() {
        List<Map<String, Object>> rows = supabaseRestClient.select(TABLE, Map.of(), "payload", null);
        return rows.stream()
            .map(r -> JavaSerializationUtils.fromBase64(String.valueOf(r.get("payload")), Reservation.class))
            .toList();
    }

    @Override
    public List<Reservation> findByTrajetId(String trajetId) {
        List<Map<String, Object>> rows = supabaseRestClient.select(TABLE, Map.of("trajet_id", "eq." + trajetId), "payload", null);
        return rows.stream()
            .map(r -> JavaSerializationUtils.fromBase64(String.valueOf(r.get("payload")), Reservation.class))
            .toList();
    }

    @Override
    public List<Reservation> findByPassagerId(String passagerId) {
        List<Map<String, Object>> rows = supabaseRestClient.select(TABLE, Map.of("passager_id", "eq." + passagerId), "payload", null);
        return rows.stream()
            .map(r -> JavaSerializationUtils.fromBase64(String.valueOf(r.get("payload")), Reservation.class))
            .toList();
    }
}
