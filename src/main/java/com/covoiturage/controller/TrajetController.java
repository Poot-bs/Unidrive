package com.covoiturage.controller;

import com.covoiturage.dto.TrajetCreateRequest;
import com.covoiturage.model.Trajet;
import com.covoiturage.service.TrajetService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/trajets")
public class TrajetController {
    private final TrajetService trajetService;

    public TrajetController(TrajetService trajetService) {
        this.trajetService = trajetService;
    }

    @PostMapping
    public Trajet proposerTrajet(@RequestBody TrajetCreateRequest request) {
        return trajetService.proposerTrajet(request);
    }

    @GetMapping
    public List<Trajet> getTrajets(
        @RequestParam(required = false) String depart,
        @RequestParam(required = false) String arrivee,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateMin,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateMax,
        @RequestParam(required = false) Double prixMax
    ) {
        return trajetService.getTrajets(depart, arrivee, dateMin, dateMax, prixMax);
    }

    @PostMapping("/{id}/close")
    public Trajet cloreTrajet(@PathVariable String id) {
        return trajetService.cloreTrajet(id);
    }
}
