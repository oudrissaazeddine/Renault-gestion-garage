package com.oudriss.Renault_gestion_garage.messaging;

import com.oudriss.Renault_gestion_garage.dto.VehicleCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class VehicleEventPublisher {

    private final KafkaTemplate<String, VehicleCreatedEvent> kafkaTemplate;

    @Value("${kafka.topics.vehicle-created}")
    private String vehicleCreatedTopic;

    public void publishVehicleCreated(VehicleCreatedEvent event) {
        log.info("Publishing VehicleCreatedEvent for vehicle: {} (garage: {})",
                event.getVehicleId(), event.getGarageId());

        CompletableFuture<SendResult<String, VehicleCreatedEvent>> future =
                kafkaTemplate.send(vehicleCreatedTopic, event.getVehicleId().toString(), event);

        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Failed to publish VehicleCreatedEvent for vehicle {}: {}",
                        event.getVehicleId(), ex.getMessage());
            } else {
                log.info("VehicleCreatedEvent published successfully for vehicle {} - partition: {}, offset: {}",
                        event.getVehicleId(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            }
        });
    }
}
