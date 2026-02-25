package com.oudriss.Renault_gestion_garage.controller;

import com.oudriss.Renault_gestion_garage.dto.VehicleRequest;
import com.oudriss.Renault_gestion_garage.dto.VehicleResponse;
import com.oudriss.Renault_gestion_garage.service.VehicleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Véhicules", description = "API de gestion des véhicules")
public class VehicleController {

    private final VehicleService vehicleService;

    @PostMapping("/api/v1/garages/{garageId}/vehicles")
    @Operation(summary = "Ajouter un véhicule à un garage")
    public ResponseEntity<VehicleResponse> addVehicle(
            @PathVariable Long garageId,
            @Valid @RequestBody VehicleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(vehicleService.addVehicleToGarage(garageId, request));
    }

    @GetMapping("/api/v1/garages/{garageId}/vehicles")
    @Operation(summary = "Lister les véhicules d'un garage")
    public ResponseEntity<List<VehicleResponse>> getVehiclesByGarage(@PathVariable Long garageId) {
        return ResponseEntity.ok(vehicleService.getVehiclesByGarage(garageId));
    }

    @GetMapping("/api/v1/vehicles/{vehicleId}")
    @Operation(summary = "Récupérer un véhicule par son ID")
    public ResponseEntity<VehicleResponse> getVehicle(@PathVariable Long vehicleId) {
        return ResponseEntity.ok(vehicleService.getVehicleById(vehicleId));
    }

    @PutMapping("/api/v1/vehicles/{vehicleId}")
    @Operation(summary = "Modifier un véhicule")
    public ResponseEntity<VehicleResponse> updateVehicle(
            @PathVariable Long vehicleId,
            @Valid @RequestBody VehicleRequest request) {
        return ResponseEntity.ok(vehicleService.updateVehicle(vehicleId, request));
    }

    @DeleteMapping("/api/v1/vehicles/{vehicleId}")
    @Operation(summary = "Supprimer un véhicule")
    public ResponseEntity<Void> deleteVehicle(@PathVariable Long vehicleId) {
        vehicleService.deleteVehicle(vehicleId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/v1/vehicles/search")
    @Operation(summary = "Rechercher des véhicules par modèle (tous garages)")
    public ResponseEntity<List<VehicleResponse>> searchVehicles(
            @RequestParam(required = false) String model,
            @RequestParam(required = false) String brand) {
        if (brand != null && model != null) {
            return ResponseEntity.ok(vehicleService.getVehiclesByBrandAndModel(brand, model));
        }
        if (model != null) {
            return ResponseEntity.ok(vehicleService.getVehiclesByModel(model));
        }
        return ResponseEntity.badRequest().build();
    }
}
