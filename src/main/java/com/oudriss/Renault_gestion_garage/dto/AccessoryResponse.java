package com.oudriss.Renault_gestion_garage.dto;

import com.oudriss.Renault_gestion_garage.entity.AccessoryType;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccessoryResponse {
    private Long id;
    private String nom;
    private String description;
    private BigDecimal prix;
    private AccessoryType type;
    private Long vehicleId;
}
