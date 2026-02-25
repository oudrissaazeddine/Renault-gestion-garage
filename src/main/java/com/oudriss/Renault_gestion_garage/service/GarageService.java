package com.oudriss.Renault_gestion_garage.service;

import com.oudriss.Renault_gestion_garage.dto.*;
import com.oudriss.Renault_gestion_garage.entity.*;
import com.oudriss.Renault_gestion_garage.exception.*;
import com.oudriss.Renault_gestion_garage.repository.GarageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class GarageService {

    private final GarageRepository garageRepository;

    public GarageResponse createGarage(GarageRequest request) {
        log.info("Creating garage: {}", request.getName());
        Garage garage = mapToEntity(request);
        Garage saved = garageRepository.save(garage);
        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public GarageResponse getGarageById(Long id) {
        Garage garage = findGarageOrThrow(id);
        return mapToResponse(garage);
    }

    @Transactional(readOnly = true)
    public PageResponse<GarageResponse> getAllGarages(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Garage> garagePage = garageRepository.findAll(pageable);

        List<GarageResponse> content = garagePage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return new PageResponse<>(content, garagePage.getNumber(), garagePage.getSize(),
                garagePage.getTotalElements(), garagePage.getTotalPages(), garagePage.isLast());
    }

    public GarageResponse updateGarage(Long id, GarageRequest request) {
        Garage garage = findGarageOrThrow(id);
        updateGarageFromRequest(garage, request);
        Garage updated = garageRepository.save(garage);
        return mapToResponse(updated);
    }

    public void deleteGarage(Long id) {
        Garage garage = findGarageOrThrow(id);
        if (!garage.getVehicles().isEmpty()) {
            throw new BusinessException("Impossible de supprimer un garage contenant des véhicules. " +
                    "Veuillez d'abord supprimer tous les véhicules.");
        }
        garageRepository.delete(garage);
        log.info("Garage deleted: {}", id);
    }

    @Transactional(readOnly = true)
    public List<GarageResponse> searchByVehicleType(VehicleType vehicleType) {
        return garageRepository.findByVehicleType(vehicleType).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<GarageResponse> searchByAccessoryType(AccessoryType accessoryType) {
        return garageRepository.findByAccessoryType(accessoryType).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<GarageResponse> searchByFuelType(FuelType fuelType) {
        return garageRepository.findByFuelType(fuelType).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<GarageResponse> searchByAccessoryName(String accessoryName) {
        return garageRepository.findByAccessoryName(accessoryName).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public Garage findGarageOrThrow(Long id) {
        return garageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Garage introuvable avec l'ID: " + id));
    }

    private Garage mapToEntity(GarageRequest request) {
        Garage garage = new Garage();
        updateGarageFromRequest(garage, request);
        return garage;
    }

    private void updateGarageFromRequest(Garage garage, GarageRequest request) {
        garage.setName(request.getName());
        garage.setAddress(request.getAddress());
        garage.setCity(request.getCity());
        garage.setPostalCode(request.getPostalCode());
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

    public GarageResponse mapToResponse(Garage garage) {
        GarageResponse response = new GarageResponse();
        response.setId(garage.getId());
        response.setName(garage.getName());
        response.setAddress(garage.getAddress());
        response.setCity(garage.getCity());
        response.setPostalCode(garage.getPostalCode());
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
