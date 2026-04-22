package com.covoiturage.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Notification implements Serializable {
    private final String id;
    private final String userId;
    private final NotificationChannel channel;
    private final String message;
    private final LocalDateTime createdAt;

    public Notification(String id, String userId, NotificationChannel channel, String message) {
        this.id = id;
        this.userId = userId;
        this.channel = channel;
        this.message = message;
        this.createdAt = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public NotificationChannel getChannel() {
        return channel;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return "Notification{" +
            "id='" + id + '\'' +
            ", userId='" + userId + '\'' +
            ", channel=" + channel +
            ", message='" + message + '\'' +
            ", createdAt=" + createdAt +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Notification that)) {
            return false;
        }
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
