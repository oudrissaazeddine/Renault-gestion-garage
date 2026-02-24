package com.oudriss.Renault_gestion_garage.service;

import com.oudriss.Renault_gestion_garage.dto.GarageRequest;
import com.oudriss.Renault_gestion_garage.dto.GarageResponse;
import com.oudriss.Renault_gestion_garage.dto.OpeningTimeDto;
import com.oudriss.Renault_gestion_garage.entity.*;
import com.oudriss.Renault_gestion_garage.repository.GarageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class GarageService {

    private final GarageRepository garageRepository;

    public GarageResponse createGarage(GarageRequest request) {
        log.info("Creating garage: {}", request.getName());
        Garage garage = new Garage();
        updateGarageFromRequest(garage, request);
        Garage savedGarage = garageRepository.save(garage);
        return mapToResponse(savedGarage);
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

    private void updateGarageFromRequest(Garage garage, GarageRequest request) {
        garage.setName(request.getName());
        garage.setAddress(request.getAddress());
        garage.setTelephone(request.getTelephone());
        garage.setEmail(request.getEmail());

        if (request.getHorairesOuverture() != null) {
            Map<DayOfWeek, List<OpeningTime>> horaires = new HashMap<>();
            request.getHorairesOuverture().forEach((day, times) -> {
                List<OpeningTime> openingTimes = times.stream()
                        .map(dto -> new OpeningTime(dto.getStartTime(), dto.getEndTime()))
                        .collect(Collectors.toList());
                horaires.put(day, openingTimes);
            });
            garage.setHorairesOuverture(horaires);
        }
    }

    private GarageResponse mapToResponse(Garage garage) {
        GarageResponse response = new GarageResponse();
        response.setId(garage.getId());
        response.setName(garage.getName());
        response.setAddress(garage.getAddress());
        response.setTelephone(garage.getTelephone());
        response.setEmail(garage.getEmail());
        response.setVehicleCount(garage.getVehicleCount());

        if (garage.getHorairesOuverture() != null) {
            Map<DayOfWeek, List<OpeningTimeDto>> horaires = new HashMap<>();
            garage.getHorairesOuverture().forEach((day, times) -> {
                List<OpeningTimeDto> dtos = times.stream()
                        .map(t -> {
                            OpeningTimeDto dto = new OpeningTimeDto();
                            dto.setStartTime(t.getStartTime());
                            dto.setEndTime(t.getEndTime());
                            return dto;
                        })
                        .collect(Collectors.toList());
                horaires.put(day, dtos);
            });
            response.setHorairesOuverture(horaires);
        }
        return response;
    }
}
