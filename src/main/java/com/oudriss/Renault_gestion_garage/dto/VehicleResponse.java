package com.oudriss.Renault_gestion_garage.dto;

import com.oudriss.Renault_gestion_garage.entity.FuelType;
import com.oudriss.Renault_gestion_garage.entity.VehicleType;
import lombok.Data;

import java.util.List;

@Data
public class VehicleResponse {
    private Long id;
    private String brand;
    private String model;
    private Integer anneeFabrication;
    private FuelType typeCarburant;
    private VehicleType vehicleType;
    private String color;
    private String licensePlate;
    private Long garageId;
    private String garageName;
    private List<AccessoryResponse> accessories;
}
