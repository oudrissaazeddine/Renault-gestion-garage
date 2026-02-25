package com.oudriss.Renault_gestion_garage.service;

import com.oudriss.Renault_gestion_garage.dto.VehicleRequest;
import com.oudriss.Renault_gestion_garage.dto.VehicleResponse;
import com.oudriss.Renault_gestion_garage.entity.*;
import com.oudriss.Renault_gestion_garage.exception.BusinessException;
import com.oudriss.Renault_gestion_garage.exception.ResourceNotFoundException;
import com.oudriss.Renault_gestion_garage.messaging.VehicleEventPublisher;
import com.oudriss.Renault_gestion_garage.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VehicleServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private GarageService garageService;

    @Mock
    private VehicleEventPublisher vehicleEventPublisher;

    @InjectMocks
    private VehicleService vehicleService;

    private Garage sampleGarage;
    private Vehicle sampleVehicle;
    private VehicleRequest validRequest;

    @BeforeEach
    void setUp() {
        sampleGarage = new Garage();
        sampleGarage.setId(1L);
        sampleGarage.setName("Garage Test");
        sampleGarage.setVehicles(new ArrayList<>());

        sampleVehicle = new Vehicle();
        sampleVehicle.setId(10L);
        sampleVehicle.setBrand("Renault");
        sampleVehicle.setModel("Clio");
        sampleVehicle.setAnneeFabrication(2022);
        sampleVehicle.setTypeCarburant(FuelType.ESSENCE);
        sampleVehicle.setVehicleType(VehicleType.BERLINE);
        sampleVehicle.setGarage(sampleGarage);
        sampleVehicle.setAccessories(new ArrayList<>());

        validRequest = new VehicleRequest();
        validRequest.setBrand("Renault");
        validRequest.setModel("Clio");
        validRequest.setAnneeFabrication(2022);
        validRequest.setTypeCarburant(FuelType.ESSENCE);
        validRequest.setVehicleType(VehicleType.BERLINE);
    }

    @Test
    void addVehicle_shouldSucceed_whenGarageHasCapacity() {
        when(garageService.findGarageOrThrow(1L)).thenReturn(sampleGarage);
        when(vehicleRepository.countByGarageId(1L)).thenReturn(10L);
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(sampleVehicle);
        doNothing().when(vehicleEventPublisher).publishVehicleCreated(any());

        VehicleResponse response = vehicleService.addVehicleToGarage(1L, validRequest);

        assertThat(response).isNotNull();
        assertThat(response.getBrand()).isEqualTo("Renault");
        assertThat(response.getModel()).isEqualTo("Clio");
        verify(vehicleEventPublisher, times(1)).publishVehicleCreated(any());
    }

    @Test
    void addVehicle_shouldThrowBusinessException_whenGarageIsFullAt50Vehicles() {
        when(garageService.findGarageOrThrow(1L)).thenReturn(sampleGarage);
        when(vehicleRepository.countByGarageId(1L)).thenReturn(50L); // max capacity

        assertThatThrownBy(() -> vehicleService.addVehicleToGarage(1L, validRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("capacité maximale")
                .hasMessageContaining("50");

        verify(vehicleRepository, never()).save(any());
        verify(vehicleEventPublisher, never()).publishVehicleCreated(any());
    }

    @Test
    void addVehicle_shouldThrowBusinessException_whenGarageHasExactly49VehiclesButCapacityReaches50() {
        when(garageService.findGarageOrThrow(1L)).thenReturn(sampleGarage);
        when(vehicleRepository.countByGarageId(1L)).thenReturn(49L); // just under limit
        when(vehicleRepository.save(any())).thenReturn(sampleVehicle);
        doNothing().when(vehicleEventPublisher).publishVehicleCreated(any());

        // Should succeed at 49
        assertThatCode(() -> vehicleService.addVehicleToGarage(1L, validRequest))
                .doesNotThrowAnyException();
    }

    @Test
    void getVehicleById_shouldReturnVehicle_whenExists() {
        when(vehicleRepository.findById(10L)).thenReturn(Optional.of(sampleVehicle));

        VehicleResponse response = vehicleService.getVehicleById(10L);

        assertThat(response.getId()).isEqualTo(10L);
        assertThat(response.getGarageId()).isEqualTo(1L);
    }

    @Test
    void getVehicleById_shouldThrowNotFoundException_whenNotExists() {
        when(vehicleRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vehicleService.getVehicleById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getVehiclesByGarage_shouldReturnList() {
        when(garageService.findGarageOrThrow(1L)).thenReturn(sampleGarage);
        when(vehicleRepository.findByGarageId(1L)).thenReturn(List.of(sampleVehicle));

        List<VehicleResponse> vehicles = vehicleService.getVehiclesByGarage(1L);

        assertThat(vehicles).hasSize(1);
        assertThat(vehicles.get(0).getBrand()).isEqualTo("Renault");
    }

    @Test
    void deleteVehicle_shouldSucceed_whenExists() {
        when(vehicleRepository.findById(10L)).thenReturn(Optional.of(sampleVehicle));

        vehicleService.deleteVehicle(10L);

        verify(vehicleRepository).delete(sampleVehicle);
    }

    @Test
    void getVehiclesByModel_shouldReturnVehiclesAcrossAllGarages() {
        when(vehicleRepository.findByModel("Clio")).thenReturn(List.of(sampleVehicle, sampleVehicle));

        List<VehicleResponse> vehicles = vehicleService.getVehiclesByModel("Clio");

        assertThat(vehicles).hasSize(2);
    }
}
