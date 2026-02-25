package com.oudriss.Renault_gestion_garage.repository;

import com.oudriss.Renault_gestion_garage.entity.Vehicle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    List<Vehicle> findByGarageId(Long garageId);

    Page<Vehicle> findByGarageId(Long garageId, Pageable pageable);

    List<Vehicle> findByModel(String model);

    List<Vehicle> findByBrandAndModel(String brand, String model);

    long countByGarageId(Long garageId);
}
