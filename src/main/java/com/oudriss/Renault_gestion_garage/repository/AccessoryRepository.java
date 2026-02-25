package com.oudriss.Renault_gestion_garage.repository;

import com.oudriss.Renault_gestion_garage.entity.Accessory;
import com.oudriss.Renault_gestion_garage.entity.AccessoryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccessoryRepository extends JpaRepository<Accessory, Long> {

    List<Accessory> findByVehicleId(Long vehicleId);

    List<Accessory> findByVehicleIdAndType(Long vehicleId, AccessoryType type);

    boolean existsByVehicleIdAndNom(Long vehicleId, String nom);
}
