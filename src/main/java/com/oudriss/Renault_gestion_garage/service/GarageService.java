package com.oudriss.Renault_gestion_garage.service;

import com.oudriss.Renault_gestion_garage.entity.*;
import com.oudriss.Renault_gestion_garage.repository.GarageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class GarageService {

    private final GarageRepository garageRepository;

    public Garage createGarage(Garage garage) {
        log.info("Creating garage: {}", garage.getName());
        Garage savedGarage = garageRepository.save(garage);
        return savedGarage;
    }


    public Optional<Garage> getGarageById(Long id) {
        Optional<Garage> garage = garageRepository.findById(id);
        return garage;
    }

    public List<Garage> getAllGarages() {
        return garageRepository.findAll();
    }

    public Optional<Garage> updateGarage(Long id, Garage garage) {
        Optional<Garage> updateGarage = garageRepository.findById(id);
        if (updateGarage.isPresent()) {
            updateGarage.get().setName(garage.getName());
            updateGarage.get().setAddress(garage.getAddress());
            updateGarage.get().setEmail(garage.getEmail());
            updateGarage.get().setTelephone(garage.getTelephone());
            updateGarage.get().setVehicles(garage.getVehicles());
            updateGarage.get().setHorairesOuverture(garage.getHorairesOuverture());
            Garage updated = garageRepository.save(updateGarage.get());
            log.info("Garage updated: {}", id);
            return Optional.of(updated);
        } else {
            return Optional.empty();
        }
    }

    public void deleteGarage(Long id) {
        Optional<Garage> garage = garageRepository.findById(id);
        if(garage.isPresent()) {
            garageRepository.delete(garage.get());
            log.info("Garage deleted: {}", id);
        }

    }
}
