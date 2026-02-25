package com.oudriss.Renault_gestion_garage.controller;

import com.oudriss.Renault_gestion_garage.dto.GarageRequest;
import com.oudriss.Renault_gestion_garage.dto.GarageResponse;
import com.oudriss.Renault_gestion_garage.dto.PageResponse;
import com.oudriss.Renault_gestion_garage.entity.AccessoryType;
import com.oudriss.Renault_gestion_garage.entity.FuelType;
import com.oudriss.Renault_gestion_garage.entity.VehicleType;
import com.oudriss.Renault_gestion_garage.service.GarageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/garages")
@RequiredArgsConstructor
@Tag(name = "Garages", description = "API de gestion des garages")
public class GarageController {

    private final GarageService garageService;

    @PostMapping
    @Operation(summary = "Créer un nouveau garage")
    public ResponseEntity<GarageResponse> createGarage(@Valid @RequestBody GarageRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(garageService.createGarage(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un garage par son ID")
    public ResponseEntity<GarageResponse> getGarage(@PathVariable Long id) {
        return ResponseEntity.ok(garageService.getGarageById(id));
    }

    @GetMapping
    @Operation(summary = "Lister tous les garages avec pagination et tri")
    public ResponseEntity<PageResponse<GarageResponse>> getAllGarages(
            @RequestParam(defaultValue = "0") @Parameter(description = "Numéro de page (0-based)") int page,
            @RequestParam(defaultValue = "10") @Parameter(description = "Taille de page") int size,
            @RequestParam(defaultValue = "name") @Parameter(description = "Champ de tri") String sortBy,
            @RequestParam(defaultValue = "asc") @Parameter(description = "Direction du tri: asc/desc") String sortDir) {
        return ResponseEntity.ok(garageService.getAllGarages(page, size, sortBy, sortDir));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier un garage")
    public ResponseEntity<GarageResponse> updateGarage(
            @PathVariable Long id,
            @Valid @RequestBody GarageRequest request) {
        return ResponseEntity.ok(garageService.updateGarage(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un garage")
    public ResponseEntity<Void> deleteGarage(@PathVariable Long id) {
        garageService.deleteGarage(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @Operation(summary = "Rechercher des garages selon des critères")
    public ResponseEntity<List<GarageResponse>> searchGarages(
            @RequestParam(required = false) VehicleType vehicleType,
            @RequestParam(required = false) AccessoryType accessoryType,
            @RequestParam(required = false) FuelType fuelType,
            @RequestParam(required = false) String accessoryName) {

        if (vehicleType != null) {
            return ResponseEntity.ok(garageService.searchByVehicleType(vehicleType));
        }
        if (accessoryType != null) {
            return ResponseEntity.ok(garageService.searchByAccessoryType(accessoryType));
        }
        if (fuelType != null) {
            return ResponseEntity.ok(garageService.searchByFuelType(fuelType));
        }
        if (accessoryName != null) {
            return ResponseEntity.ok(garageService.searchByAccessoryName(accessoryName));
        }

        // No filter: return all
        PageResponse<GarageResponse> all = garageService.getAllGarages(0, Integer.MAX_VALUE, "name", "asc");
        return ResponseEntity.ok(all.getContent());
    }


}
