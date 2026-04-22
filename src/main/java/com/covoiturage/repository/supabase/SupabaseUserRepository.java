package com.covoiturage.repository.supabase;

import com.covoiturage.model.User;
import com.covoiturage.repository.UserRepository;
import com.covoiturage.repository.serialization.JavaSerializationUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@ConditionalOnProperty(name = "app.persistence.mode", havingValue = "supabase")
public class SupabaseUserRepository implements UserRepository {
    private static final String TABLE = "users_store";

    private final SupabaseRestClient supabaseRestClient;

    public SupabaseUserRepository(SupabaseRestClient supabaseRestClient) {
        this.supabaseRestClient = supabaseRestClient;
    }

    @Override
    public User save(User user) {
        String payload = JavaSerializationUtils.toBase64(user);
        supabaseRestClient.upsert(TABLE, Map.of(
            "id", user.getIdentifiant(),
            "email", user.getEmail(),
            "payload", payload
        ), "id");
        return user;
    }

    @Override
    public Optional<User> findById(String id) {
        List<Map<String, Object>> rows = supabaseRestClient.select(TABLE, Map.of("id", "eq." + id), "payload", 1);
        return rows.stream()
            .findFirst()
            .map(r -> JavaSerializationUtils.fromBase64(String.valueOf(r.get("payload")), User.class));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        List<Map<String, Object>> rows = supabaseRestClient.select(TABLE, Map.of("email", "eq." + email), "payload", 1);
        return rows.stream()
            .findFirst()
            .map(r -> JavaSerializationUtils.fromBase64(String.valueOf(r.get("payload")), User.class));
    }

    @Override
    public List<User> findAll() {
        List<Map<String, Object>> rows = supabaseRestClient.select(TABLE, Map.of(), "payload", null);
        return rows.stream()
            .map(r -> JavaSerializationUtils.fromBase64(String.valueOf(r.get("payload")), User.class))
            .toList();
    }
}
