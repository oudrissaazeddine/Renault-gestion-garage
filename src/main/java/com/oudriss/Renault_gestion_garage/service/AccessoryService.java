package com.oudriss.Renault_gestion_garage.service;

import com.oudriss.Renault_gestion_garage.dto.AccessoryRequest;
import com.oudriss.Renault_gestion_garage.dto.AccessoryResponse;
import com.oudriss.Renault_gestion_garage.entity.Accessory;
import com.oudriss.Renault_gestion_garage.entity.Vehicle;
import com.oudriss.Renault_gestion_garage.exception.ResourceNotFoundException;
import com.oudriss.Renault_gestion_garage.repository.AccessoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AccessoryService {

    private final AccessoryRepository accessoryRepository;
    private final VehicleService vehicleService;

    public AccessoryResponse addAccessory(Long vehicleId, AccessoryRequest request) {
        Vehicle vehicle = vehicleService.findVehicleOrThrow(vehicleId);
        Accessory accessory = Accessory.builder()
                .nom(request.getNom())
                .description(request.getDescription())
                .prix(request.getPrix())
                .type(request.getType())
                .vehicle(vehicle)
                .build();
        Accessory saved = accessoryRepository.save(accessory);
        log.info("Accessory '{}' added to vehicle {}", saved.getNom(), vehicleId);
        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<AccessoryResponse> getAccessoriesByVehicle(Long vehicleId) {
        vehicleService.findVehicleOrThrow(vehicleId); // Validate vehicle exists
        return accessoryRepository.findByVehicleId(vehicleId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AccessoryResponse getAccessoryById(Long accessoryId) {
        return mapToResponse(findAccessoryOrThrow(accessoryId));
    }

    public AccessoryResponse updateAccessory(Long accessoryId, AccessoryRequest request) {
        Accessory accessory = findAccessoryOrThrow(accessoryId);
        accessory.setNom(request.getNom());
        accessory.setDescription(request.getDescription());
        accessory.setPrix(request.getPrix());
        accessory.setType(request.getType());
        return mapToResponse(accessoryRepository.save(accessory));
    }

    public void deleteAccessory(Long accessoryId) {
        Accessory accessory = findAccessoryOrThrow(accessoryId);
        accessoryRepository.delete(accessory);
        log.info("Accessory {} deleted", accessoryId);
    }

    private Accessory findAccessoryOrThrow(Long id) {
        return accessoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Accessoire introuvable avec l'ID: " + id));
    }

    private AccessoryResponse mapToResponse(Accessory accessory) {
        AccessoryResponse response = new AccessoryResponse();
        response.setId(accessory.getId());
        response.setNom(accessory.getNom());
        response.setDescription(accessory.getDescription());
        response.setPrix(accessory.getPrix());
        response.setType(accessory.getType());
        response.setVehicleId(accessory.getVehicle().getId());
        return response;
    }
}
