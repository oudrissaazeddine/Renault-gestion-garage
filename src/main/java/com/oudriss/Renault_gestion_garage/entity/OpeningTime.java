package com.oudriss.Renault_gestion_garage.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OpeningTime {
    private LocalTime startTime;
    private LocalTime endTime;
}
