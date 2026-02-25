package com.oudriss.Renault_gestion_garage.repository;

import com.oudriss.Renault_gestion_garage.entity.AccessoryType;
import com.oudriss.Renault_gestion_garage.entity.FuelType;
import com.oudriss.Renault_gestion_garage.entity.Garage;
import com.oudriss.Renault_gestion_garage.entity.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GarageRepository extends JpaRepository<Garage, Long> {

    @Query("SELECT DISTINCT g FROM Garage g JOIN g.vehicles v WHERE v.vehicleType = :vehicleType")
    List<Garage> findByVehicleType(@Param("vehicleType") VehicleType vehicleType);

    @Query("SELECT DISTINCT g FROM Garage g " +
           "JOIN g.vehicles v " +
           "JOIN v.accessories a " +
           "WHERE a.type = :accessoryType")
    List<Garage> findByAccessoryType(@Param("accessoryType") AccessoryType accessoryType);

    @Query("SELECT DISTINCT g FROM Garage g " +
           "JOIN g.vehicles v " +
           "JOIN v.accessories a " +
           "WHERE a.nom = :accessoryName")
    List<Garage> findByAccessoryName(@Param("accessoryName") String accessoryName);

    @Query("SELECT DISTINCT g FROM Garage g " +
           "JOIN g.vehicles v " +
           "WHERE v.typeCarburant = :fuelType")
    List<Garage> findByFuelType(@Param("fuelType") FuelType fuelType);

    boolean existsByEmail(String email);
}
