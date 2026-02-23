package com.oudriss.Renault_gestion_garage.repository;

import com.oudriss.Renault_gestion_garage.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {


}
