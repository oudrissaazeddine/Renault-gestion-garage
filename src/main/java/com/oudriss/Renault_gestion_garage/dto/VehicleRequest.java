package com.oudriss.Renault_gestion_garage.dto;

import com.oudriss.Renault_gestion_garage.entity.FuelType;
import com.oudriss.Renault_gestion_garage.entity.VehicleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VehicleRequest {

    @NotBlank(message = "La marque est obligatoire")
    private String brand;

    @NotBlank(message = "Le modèle est obligatoire")
    private String model;

    @NotNull(message = "L'année de fabrication est obligatoire")
    private Integer anneeFabrication;

    @NotNull(message = "Le type de carburant est obligatoire")
    private FuelType typeCarburant;

    private VehicleType vehicleType;
}
