package com.oudriss.Renault_gestion_garage.service;


import com.oudriss.Renault_gestion_garage.entity.Vehicle;
import com.oudriss.Renault_gestion_garage.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class VehicleService {

    private static final int MAX_VEHICLES_PER_GARAGE = 50;

    private final VehicleRepository vehicleRepository;
    private final GarageService garageService;

    public Vehicle addVehicle(Vehicle vehicle) {

        Vehicle saved = vehicleRepository.save(vehicle);
        log.info("Vehicle {} {} a été ajouter avec success au garage {}", saved.getBrand(), saved.getModel(), saved.getGarage().getName());

        return saved;
    }

    public Vehicle getVehicleById(Long vehicleId) {
        Optional<Vehicle> vehicle = vehicleRepository.findById(vehicleId);
        if (vehicle.isPresent()) {
            return vehicle.get();
        }
        return null;
    }
}
