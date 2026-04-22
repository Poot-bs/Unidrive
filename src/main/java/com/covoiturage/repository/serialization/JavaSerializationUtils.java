package com.covoiturage.repository.serialization;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Base64;

public final class JavaSerializationUtils {
    private JavaSerializationUtils() {
    }

    public static String toBase64(Object obj) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            try (ObjectOutputStream oos = new ObjectOutputStream(bos)) {
                oos.writeObject(obj);
            }
            return Base64.getEncoder().encodeToString(bos.toByteArray());
        } catch (IOException ex) {
            throw new IllegalStateException("Impossible de serialiser l'objet", ex);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T fromBase64(String value, Class<T> targetType) {
        try {
            byte[] data = Base64.getDecoder().decode(value);
            try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data))) {
                Object obj = ois.readObject();
                return (T) obj;
            }
        } catch (IOException | ClassNotFoundException ex) {
            throw new IllegalStateException("Impossible de deserialiser l'objet", ex);
        }
    }
}
