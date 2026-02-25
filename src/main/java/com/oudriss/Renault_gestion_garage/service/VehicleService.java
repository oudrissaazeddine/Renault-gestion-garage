package com.oudriss.Renault_gestion_garage.service;

import com.oudriss.Renault_gestion_garage.dto.AccessoryResponse;
import com.oudriss.Renault_gestion_garage.dto.VehicleCreatedEvent;
import com.oudriss.Renault_gestion_garage.dto.VehicleRequest;
import com.oudriss.Renault_gestion_garage.dto.VehicleResponse;
import com.oudriss.Renault_gestion_garage.entity.Garage;
import com.oudriss.Renault_gestion_garage.entity.Vehicle;
import com.oudriss.Renault_gestion_garage.exception.BusinessException;
import com.oudriss.Renault_gestion_garage.exception.ResourceNotFoundException;
import com.oudriss.Renault_gestion_garage.messaging.VehicleEventPublisher;
import com.oudriss.Renault_gestion_garage.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class VehicleService {

    private static final int MAX_VEHICLES_PER_GARAGE = 50;

    private final VehicleRepository vehicleRepository;
    private final GarageService garageService;
    private final VehicleEventPublisher vehicleEventPublisher;

    public VehicleResponse addVehicleToGarage(Long garageId, VehicleRequest request) {
        Garage garage = garageService.findGarageOrThrow(garageId);

        // Business constraint: max 50 vehicles per garage
        long currentCount = vehicleRepository.countByGarageId(garageId);
        if (currentCount >= MAX_VEHICLES_PER_GARAGE) {
            throw new BusinessException(String.format(
                    "Le garage '%s' a atteint sa capacité maximale de %d véhicules.",
                    garage.getName(), MAX_VEHICLES_PER_GARAGE));
        }

        Vehicle vehicle = mapToEntity(request, garage);
        Vehicle saved = vehicleRepository.save(vehicle);
        log.info("Vehicle {} {} added to garage {}", saved.getBrand(), saved.getModel(), garageId);

        // Publish event
        VehicleCreatedEvent event = VehicleCreatedEvent.builder()
                .vehicleId(saved.getId())
                .brand(saved.getBrand())
                .model(saved.getModel())
                .anneeFabrication(saved.getAnneeFabrication())
                .typeCarburant(saved.getTypeCarburant())
                .vehicleType(saved.getVehicleType())
                .garageId(garage.getId())
                .garageName(garage.getName())
                .createdAt(LocalDateTime.now())
                .build();
        vehicleEventPublisher.publishVehicleCreated(event);

        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<VehicleResponse> getVehiclesByGarage(Long garageId) {
        garageService.findGarageOrThrow(garageId); // Validate garage exists
        return vehicleRepository.findByGarageId(garageId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public VehicleResponse getVehicleById(Long vehicleId) {
        Vehicle vehicle = findVehicleOrThrow(vehicleId);
        return mapToResponse(vehicle);
    }

    public VehicleResponse updateVehicle(Long vehicleId, VehicleRequest request) {
        Vehicle vehicle = findVehicleOrThrow(vehicleId);
        updateFromRequest(vehicle, request);
        Vehicle updated = vehicleRepository.save(vehicle);
        return mapToResponse(updated);
    }

    public void deleteVehicle(Long vehicleId) {
        Vehicle vehicle = findVehicleOrThrow(vehicleId);
        vehicleRepository.delete(vehicle);
        log.info("Vehicle {} deleted", vehicleId);
    }

    @Transactional(readOnly = true)
    public List<VehicleResponse> getVehiclesByModel(String model) {
        return vehicleRepository.findByModel(model).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<VehicleResponse> getVehiclesByBrandAndModel(String brand, String model) {
        return vehicleRepository.findByBrandAndModel(brand, model).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public Vehicle findVehicleOrThrow(Long id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Véhicule introuvable avec l'ID: " + id));
    }

    private Vehicle mapToEntity(VehicleRequest request, Garage garage) {
        return Vehicle.builder()
                .brand(request.getBrand())
                .model(request.getModel())
                .anneeFabrication(request.getAnneeFabrication())
                .typeCarburant(request.getTypeCarburant())
                .vehicleType(request.getVehicleType())
                .color(request.getColor())
                .licensePlate(request.getLicensePlate())
                .garage(garage)
                .build();
    }

    private void updateFromRequest(Vehicle vehicle, VehicleRequest request) {
        vehicle.setBrand(request.getBrand());
        vehicle.setModel(request.getModel());
        vehicle.setAnneeFabrication(request.getAnneeFabrication());
        vehicle.setTypeCarburant(request.getTypeCarburant());
        vehicle.setVehicleType(request.getVehicleType());
        vehicle.setColor(request.getColor());
        vehicle.setLicensePlate(request.getLicensePlate());
    }

    public VehicleResponse mapToResponse(Vehicle vehicle) {
        VehicleResponse response = new VehicleResponse();
        response.setId(vehicle.getId());
        response.setBrand(vehicle.getBrand());
        response.setModel(vehicle.getModel());
        response.setAnneeFabrication(vehicle.getAnneeFabrication());
        response.setTypeCarburant(vehicle.getTypeCarburant());
        response.setVehicleType(vehicle.getVehicleType());
        response.setColor(vehicle.getColor());
        response.setLicensePlate(vehicle.getLicensePlate());
        response.setGarageId(vehicle.getGarage().getId());
        response.setGarageName(vehicle.getGarage().getName());

        if (vehicle.getAccessories() != null) {
            List<AccessoryResponse> accessories = vehicle.getAccessories().stream()
                    .map(a -> {
                        AccessoryResponse ar = new AccessoryResponse();
                        ar.setId(a.getId());
                        ar.setNom(a.getNom());
                        ar.setDescription(a.getDescription());
                        ar.setPrix(a.getPrix());
                        ar.setType(a.getType());
                        ar.setVehicleId(vehicle.getId());
                        return ar;
                    })
                    .collect(Collectors.toList());
            response.setAccessories(accessories);
        }
        return response;
    }
}
