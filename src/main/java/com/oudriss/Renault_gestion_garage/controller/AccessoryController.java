package com.oudriss.Renault_gestion_garage.controller;

import com.oudriss.Renault_gestion_garage.dto.AccessoryRequest;
import com.oudriss.Renault_gestion_garage.dto.AccessoryResponse;
import com.oudriss.Renault_gestion_garage.service.AccessoryService;
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
@Tag(name = "Accessoires", description = "API de gestion des accessoires")
public class AccessoryController {

    private final AccessoryService accessoryService;

    @PostMapping("/api/v1/vehicles/{vehicleId}/accessories")
    @Operation(summary = "Ajouter un accessoire à un véhicule")
    public ResponseEntity<AccessoryResponse> addAccessory(
            @PathVariable Long vehicleId,
            @Valid @RequestBody AccessoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(accessoryService.addAccessory(vehicleId, request));
    }

    @GetMapping("/api/v1/vehicles/{vehicleId}/accessories")
    @Operation(summary = "Lister les accessoires d'un véhicule")
    public ResponseEntity<List<AccessoryResponse>> getAccessories(@PathVariable Long vehicleId) {
        return ResponseEntity.ok(accessoryService.getAccessoriesByVehicle(vehicleId));
    }

    @GetMapping("/api/v1/accessories/{accessoryId}")
    @Operation(summary = "Récupérer un accessoire par son ID")
    public ResponseEntity<AccessoryResponse> getAccessory(@PathVariable Long accessoryId) {
        return ResponseEntity.ok(accessoryService.getAccessoryById(accessoryId));
    }

    @PutMapping("/api/v1/accessories/{accessoryId}")
    @Operation(summary = "Modifier un accessoire")
    public ResponseEntity<AccessoryResponse> updateAccessory(
            @PathVariable Long accessoryId,
            @Valid @RequestBody AccessoryRequest request) {
        return ResponseEntity.ok(accessoryService.updateAccessory(accessoryId, request));
    }

    @DeleteMapping("/api/v1/accessories/{accessoryId}")
    @Operation(summary = "Supprimer un accessoire")
    public ResponseEntity<Void> deleteAccessory(@PathVariable Long accessoryId) {
        accessoryService.deleteAccessory(accessoryId);
        return ResponseEntity.noContent().build();
    }
}
