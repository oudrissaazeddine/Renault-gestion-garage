package com.oudriss.Renault_gestion_garage.dto;

import com.oudriss.Renault_gestion_garage.entity.AccessoryType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccessoryRequest {

    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    @NotBlank(message = "La description est obligatoire")
    private String description;

    @NotNull(message = "Le prix est obligatoire")
    private BigDecimal prix;

    @NotNull(message = "Le type est obligatoire")
    private AccessoryType type;
}
