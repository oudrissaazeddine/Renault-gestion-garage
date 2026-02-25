package com.oudriss.Renault_gestion_garage.integration;

import com.oudriss.Renault_gestion_garage.dto.*;
import com.oudriss.Renault_gestion_garage.entity.FuelType;
import com.oudriss.Renault_gestion_garage.entity.VehicleType;
import com.oudriss.Renault_gestion_garage.repository.GarageRepository;
import com.oudriss.Renault_gestion_garage.repository.VehicleRepository;
import com.oudriss.Renault_gestion_garage.service.GarageService;
import com.oudriss.Renault_gestion_garage.service.VehicleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = {"vehicle-created-topic"})
@DirtiesContext
@ActiveProfiles("test")
class GarageVehicleIntegrationTest {

    @Autowired
    private GarageService garageService;

    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private GarageRepository garageRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    private GarageResponse createdGarage;

    @BeforeEach
    void setUp() {
        vehicleRepository.deleteAll();
        garageRepository.deleteAll();

        OpeningTimeDto openingTime = new OpeningTimeDto();
        openingTime.setStartTime(LocalTime.of(8, 0));
        openingTime.setEndTime(LocalTime.of(18, 0));

        GarageRequest garageRequest = new GarageRequest();
        garageRequest.setName("Garage Intégration");
        garageRequest.setAddress("1 Rue Integration");
        garageRequest.setCity("Lyon");
        garageRequest.setTelephone("0456789012");
        garageRequest.setEmail("integration@test.fr");
        garageRequest.setHorairesOuverture(Map.of(DayOfWeek.MONDAY, List.of(openingTime)));

        createdGarage = garageService.createGarage(garageRequest);
    }

    @Test
    void fullFlow_createGarageAndVehicle_shouldPersistCorrectly() {
        // Given
        VehicleRequest vehicleRequest = new VehicleRequest();
        vehicleRequest.setBrand("Peugeot");
        vehicleRequest.setModel("308");
        vehicleRequest.setAnneeFabrication(2023);
        vehicleRequest.setTypeCarburant(FuelType.HYBRIDE);
        vehicleRequest.setVehicleType(VehicleType.BERLINE);

        // When
        VehicleResponse vehicle = vehicleService.addVehicleToGarage(createdGarage.getId(), vehicleRequest);

        // Then
        assertThat(vehicle.getId()).isNotNull();
        assertThat(vehicle.getBrand()).isEqualTo("Peugeot");
        assertThat(vehicle.getGarageId()).isEqualTo(createdGarage.getId());
        assertThat(vehicle.getGarageName()).isEqualTo("Garage Intégration");

        // Verify garage vehicle count
        GarageResponse updatedGarage = garageService.getGarageById(createdGarage.getId());
        assertThat(updatedGarage.getVehicleCount()).isEqualTo(1);
    }

    @Test
    void quota_shouldEnforceMaximum50VehiclesPerGarage() {
        // Fill garage to 49 vehicles
        for (int i = 0; i < 49; i++) {
            VehicleRequest req = new VehicleRequest();
            req.setBrand("Brand" + i);
            req.setModel("Model" + i);
            req.setAnneeFabrication(2020);
            req.setTypeCarburant(FuelType.DIESEL);
            vehicleService.addVehicleToGarage(createdGarage.getId(), req);
        }

        // 50th vehicle should succeed
        VehicleRequest fiftiethVehicle = new VehicleRequest();
        fiftiethVehicle.setBrand("LastBrand");
        fiftiethVehicle.setModel("LastModel");
        fiftiethVehicle.setAnneeFabrication(2023);
        fiftiethVehicle.setTypeCarburant(FuelType.ELECTRIQUE);
        assertThatCode(() -> vehicleService.addVehicleToGarage(createdGarage.getId(), fiftiethVehicle))
                .doesNotThrowAnyException();

        // 51st vehicle should fail
        VehicleRequest fiftyFirstVehicle = new VehicleRequest();
        fiftyFirstVehicle.setBrand("TooMany");
        fiftyFirstVehicle.setModel("Overflow");
        fiftyFirstVehicle.setAnneeFabrication(2023);
        fiftyFirstVehicle.setTypeCarburant(FuelType.ESSENCE);

        assertThatThrownBy(() -> vehicleService.addVehicleToGarage(createdGarage.getId(), fiftyFirstVehicle))
                .isInstanceOf(com.oudriss.Renault_gestion_garage.exception.BusinessException.class)
                .hasMessageContaining("50");
    }

    @Test
    void sameModelInMultipleGarages_shouldBeAllowed() {
        // Create second garage
        OpeningTimeDto ot = new OpeningTimeDto();
        ot.setStartTime(LocalTime.of(9, 0));
        ot.setEndTime(LocalTime.of(17, 0));

        GarageRequest g2Request = new GarageRequest();
        g2Request.setName("Garage 2");
        g2Request.setAddress("2 Rue Deux");
        g2Request.setTelephone("0612345678");
        g2Request.setEmail("garage2@test.fr");
        g2Request.setHorairesOuverture(Map.of(DayOfWeek.FRIDAY, List.of(ot)));
        GarageResponse garage2 = garageService.createGarage(g2Request);

        // Add same model to both garages
        VehicleRequest clioRequest = new VehicleRequest();
        clioRequest.setBrand("Renault");
        clioRequest.setModel("Clio");
        clioRequest.setAnneeFabrication(2022);
        clioRequest.setTypeCarburant(FuelType.ESSENCE);

        VehicleResponse v1 = vehicleService.addVehicleToGarage(createdGarage.getId(), clioRequest);
        VehicleResponse v2 = vehicleService.addVehicleToGarage(garage2.getId(), clioRequest);

        // Both exist
        assertThat(v1.getGarageId()).isEqualTo(createdGarage.getId());
        assertThat(v2.getGarageId()).isEqualTo(garage2.getId());

        // Search by model returns vehicles from both garages
        List<VehicleResponse> clios = vehicleService.getVehiclesByModel("Clio");
        assertThat(clios).hasSize(2);
        assertThat(clios.stream().map(VehicleResponse::getGarageId))
                .containsExactlyInAnyOrder(createdGarage.getId(), garage2.getId());
    }
}
