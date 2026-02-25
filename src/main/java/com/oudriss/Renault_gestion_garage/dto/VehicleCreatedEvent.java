package com.oudriss.Renault_gestion_garage.dto;

import com.oudriss.Renault_gestion_garage.entity.FuelType;
import com.oudriss.Renault_gestion_garage.entity.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleCreatedEvent {
    private Long vehicleId;
    private String brand;
    private String model;
    private Integer anneeFabrication;
    private FuelType typeCarburant;
    private VehicleType vehicleType;
    private Long garageId;
    private String garageName;
    private LocalDateTime createdAt;
}
