package com.oudriss.Renault_gestion_garage.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vehicles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "La marque est obligatoire")
    @Column(nullable = false)
    private String brand;

    @NotBlank(message = "Le modèle est obligatoire")
    @Column(nullable = false)
    private String model;

    @NotNull(message = "L'année de fabrication est obligatoire")
    @Column(nullable = false, name = "annee_fabrication")
    private Integer anneeFabrication;

    @NotNull(message = "Le type de carburant est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "type_carburant")
    private FuelType typeCarburant;

    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_type")
    private VehicleType vehicleType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "garage_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Garage garage;

    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Accessory> accessories = new ArrayList<>();
}
