package com.oudriss.Renault_gestion_garage.controller;


import com.oudriss.Renault_gestion_garage.entity.Garage;
import com.oudriss.Renault_gestion_garage.service.GarageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/garages")
public class GarageController {

    @Autowired
    private GarageService garageService;

    @PostMapping
    public ResponseEntity<Garage> createGarage(@RequestBody Garage garage) {
        Garage created = garageService.createGarage(garage);
        if (created == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(created);
        }
    }

    @PostMapping("/{id}")
    public ResponseEntity updateGarage(@PathVariable Long id, @RequestBody Garage garage) {
        Optional<Garage> updateGarage = garageService.updateGarage(id, garage);
        if (updateGarage.isPresent()) {
            return ResponseEntity.ok(updateGarage.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteGarage(@PathVariable Long id) {
        garageService.deleteGarage(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<Garage>> getAllGarages() {
        List<Garage> garages = garageService.getAllGarages();
        if (garages.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(garages);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Garage> getGarageById(@PathVariable Long id) {
        Optional<Garage> garage = garageService.getGarageById(id);
        if (garage.isPresent()) {
            return ResponseEntity.ok(garage.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
