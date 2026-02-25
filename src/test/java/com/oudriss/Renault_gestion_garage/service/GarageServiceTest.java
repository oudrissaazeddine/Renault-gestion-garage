package com.oudriss.Renault_gestion_garage.service;

import com.oudriss.Renault_gestion_garage.dto.GarageRequest;
import com.oudriss.Renault_gestion_garage.dto.GarageResponse;
import com.oudriss.Renault_gestion_garage.dto.OpeningTimeDto;
import com.oudriss.Renault_gestion_garage.entity.Garage;
import com.oudriss.Renault_gestion_garage.exception.BusinessException;
import com.oudriss.Renault_gestion_garage.exception.ResourceNotFoundException;
import com.oudriss.Renault_gestion_garage.repository.GarageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GarageServiceTest {

    @Mock
    private GarageRepository garageRepository;

    @InjectMocks
    private GarageService garageService;

    private GarageRequest validRequest;
    private Garage sampleGarage;

    @BeforeEach
    void setUp() {
        validRequest = new GarageRequest();
        validRequest.setName("Garage Centrale");
        validRequest.setAddress("10 Rue oudriss");
        validRequest.setCity("Rabat");
        validRequest.setPostalCode("75001");
        validRequest.setTelephone("0123456789");
        validRequest.setEmail("contact@Renault.com");

        OpeningTimeDto openingTime = new OpeningTimeDto();
        openingTime.setStartTime(LocalTime.of(8, 0));
        openingTime.setEndTime(LocalTime.of(18, 0));

        validRequest.setHorairesOuverture(Map.of(
                DayOfWeek.MONDAY, List.of(openingTime),
                DayOfWeek.TUESDAY, List.of(openingTime)
        ));

        sampleGarage = new Garage();
        sampleGarage.setId(1L);
        sampleGarage.setName("Garage Centrale");
        sampleGarage.setAddress("10 Rue oudriss");
        sampleGarage.setCity("Rabat");
        sampleGarage.setEmail("contact@renault.com");
        sampleGarage.setTelephone("0123456789");
        sampleGarage.setHorairesOuverture(new HashMap<>());
        sampleGarage.setVehicles(new ArrayList<>());
    }

    @Test
    void createGarage_shouldReturnGarageResponse_whenValidRequest() {
        when(garageRepository.save(any(Garage.class))).thenReturn(sampleGarage);

        GarageResponse response = garageService.createGarage(validRequest);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Garage Centrale");
        verify(garageRepository, times(1)).save(any(Garage.class));
    }

    @Test
    void getGarageById_shouldReturnGarage_whenExists() {
        when(garageRepository.findById(1L)).thenReturn(Optional.of(sampleGarage));

        GarageResponse response = garageService.getGarageById(1L);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Garage Centrale");
    }

    @Test
    void getGarageById_shouldThrowNotFoundException_whenNotExists() {
        when(garageRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> garageService.getGarageById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void getAllGarages_shouldReturnPagedResponse() {
        List<Garage> garages = List.of(sampleGarage);
        Page<Garage> page = new PageImpl<>(garages);
        when(garageRepository.findAll(any(Pageable.class))).thenReturn(page);

        var response = garageService.getAllGarages(0, 10, "name", "asc");

        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getTotalElements()).isEqualTo(1);
    }

    @Test
    void updateGarage_shouldUpdateFields_whenValidData() {
        when(garageRepository.findById(1L)).thenReturn(Optional.of(sampleGarage));
        when(garageRepository.save(any(Garage.class))).thenReturn(sampleGarage);

        validRequest.setName("Nouveau Nom");
        GarageResponse response = garageService.updateGarage(1L, validRequest);

        verify(garageRepository).save(any(Garage.class));
        assertThat(response).isNotNull();
    }

    @Test
    void deleteGarage_shouldSucceed_whenNoVehicles() {
        sampleGarage.setVehicles(new ArrayList<>());
        when(garageRepository.findById(1L)).thenReturn(Optional.of(sampleGarage));

        garageService.deleteGarage(1L);

        verify(garageRepository).delete(sampleGarage);
    }

    @Test
    void deleteGarage_shouldThrowBusinessException_whenHasVehicles() {
        sampleGarage.getVehicles().add(new com.oudriss.Renault_gestion_garage.entity.Vehicle());
        when(garageRepository.findById(1L)).thenReturn(Optional.of(sampleGarage));

        assertThatThrownBy(() -> garageService.deleteGarage(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("véhicules");
    }
}
