package com.covoiturage.repository.supabase;

import com.covoiturage.exception.ValidationException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

@Component
@ConditionalOnProperty(name = "app.persistence.mode", havingValue = "supabase")
public class SupabaseRestClient {
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String restBaseUrl;
    private final String serviceKey;

    public SupabaseRestClient(@Value("${supabase.url:}") String supabaseUrl,
                              @Value("${supabase.service-key:}") String serviceKey,
                              @Value("${supabase.schema:public}") String schema,
                              ObjectMapper objectMapper) {
        if (isBlank(supabaseUrl) || isBlank(serviceKey)) {
            throw new ValidationException("SUPABASE_URL et SUPABASE_SERVICE_KEY sont obligatoires en mode supabase");
        }
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = objectMapper;
        String normalizedUrl = supabaseUrl.endsWith("/") ? supabaseUrl.substring(0, supabaseUrl.length() - 1) : supabaseUrl;
        this.restBaseUrl = normalizedUrl + "/rest/v1/";
        this.serviceKey = serviceKey;
        this.defaultSchema = schema;
    }

    private final String defaultSchema;

    public void upsert(String table, Map<String, Object> row, String onConflict) {
        try {
            String query = isBlank(onConflict) ? "" : "?on_conflict=" + encode(onConflict);
            URI uri = URI.create(restBaseUrl + table + query);
            String body = objectMapper.writeValueAsString(List.of(row));

            HttpRequest request = baseRequest(uri)
                .header("Prefer", "resolution=merge-duplicates,return=minimal")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            ensureSuccess(response, "upsert", table);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Erreur reseau Supabase", ex);
        } catch (IOException ex) {
            throw new IllegalStateException("Erreur reseau Supabase", ex);
        }
    }

    public List<Map<String, Object>> select(String table, Map<String, String> filters, String selectColumns, Integer limit) {
        try {
            StringJoiner joiner = new StringJoiner("&");
            joiner.add("select=" + encode(selectColumns == null ? "*" : selectColumns));
            if (limit != null && limit > 0) {
                joiner.add("limit=" + limit);
            }
            if (filters != null) {
                filters.forEach((k, v) -> joiner.add(encode(k) + "=" + encode(v)));
            }

            URI uri = URI.create(restBaseUrl + table + "?" + joiner);
            HttpRequest request = baseRequest(uri).GET().build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            ensureSuccess(response, "select", table);

            JsonNode root = objectMapper.readTree(response.body());
            return objectMapper.convertValue(root, new TypeReference<>() {
            });
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Erreur reseau Supabase", ex);
        } catch (IOException ex) {
            throw new IllegalStateException("Erreur reseau Supabase", ex);
        }
    }

    private HttpRequest.Builder baseRequest(URI uri) {
        return HttpRequest.newBuilder(uri)
            .header("apikey", serviceKey)
            .header("Authorization", "Bearer " + serviceKey)
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .header("Accept-Profile", defaultSchema)
            .header("Content-Profile", defaultSchema);
    }

    private void ensureSuccess(HttpResponse<String> response, String operation, String table) {
        int status = response.statusCode();
        if (status >= 200 && status < 300) {
            return;
        }
        throw new IllegalStateException("Supabase " + operation + " erreur sur " + table + ": " + status + " - " + response.body());
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
