package com.oudriss.Renault_gestion_garage.repository;


import com.oudriss.Renault_gestion_garage.entity.Garage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GarageRepository extends JpaRepository<Garage, Long> {

}
