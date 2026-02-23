package com.oudriss.Renault_gestion_garage.service;

import com.oudriss.Renault_gestion_garage.entity.Accessory;
import com.oudriss.Renault_gestion_garage.repository.AccessoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AccessoryService {

    private final AccessoryRepository accessoryRepository;

    public Accessory addAccessory(Accessory accessory) {

        Accessory saved = accessoryRepository.save(accessory);
        log.info("Accessory '{}' added to vehicle {} {}", saved.getNom(), accessory.getVehicle().getModel(), accessory.getVehicle().getBrand());
        return saved;
    }


}
