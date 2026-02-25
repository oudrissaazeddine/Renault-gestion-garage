package com.oudriss.Renault_gestion_garage.dto;

import lombok.Data;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;

@Data
public class GarageResponse {
    private Long id;
    private String name;
    private String address;
    private String city;
    private String postalCode;
    private String telephone;
    private String email;
    private Map<DayOfWeek, List<OpeningTimeDto>> horairesOuverture;
    private int vehicleCount;
}
