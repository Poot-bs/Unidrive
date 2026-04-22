package com.covoiturage.repository;

import com.covoiturage.model.User;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@ConditionalOnProperty(name = "app.persistence.mode", havingValue = "memory", matchIfMissing = true)
public class InMemoryUserRepository implements UserRepository {
    private final Map<String, User> usersById = new ConcurrentHashMap<>();
    private final Map<String, String> idByEmail = new ConcurrentHashMap<>();

    @Override
    public User save(User user) {
        usersById.put(user.getIdentifiant(), user);
        idByEmail.put(user.getEmail(), user.getIdentifiant());
        return user;
    }

    @Override
    public Optional<User> findById(String id) {
        return Optional.ofNullable(usersById.get(id));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        String id = idByEmail.get(email);
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(usersById.get(id));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(usersById.values());
    }
}
