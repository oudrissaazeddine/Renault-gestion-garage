package com.oudriss.Renault_gestion_garage.messaging;

import com.oudriss.Renault_gestion_garage.dto.VehicleCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class VehicleEventConsumer {

    @KafkaListener(
            topics = "${kafka.topics.vehicle-created}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consumeVehicleCreated(
            @Payload VehicleCreatedEvent event,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {

        log.info("Received VehicleCreatedEvent - vehicleId: {}, garage: '{}', " +
                        "brand: {} {}, fuel: {}, partition: {}, offset: {}",
                event.getVehicleId(),
                event.getGarageName(),
                event.getBrand(),
                event.getModel(),
                event.getTypeCarburant(),
                partition,
                offset);

        processVehicleCreatedEvent(event);
    }

    private void processVehicleCreatedEvent(VehicleCreatedEvent event) {
        // Business logic for handling new vehicle events:
        // - Notify external systems
        // - Update statistics / dashboards
        // - Trigger warranty registrations
        // - Send notifications to fleet managers
        log.info("Processing vehicle {} added to garage {} at {}",
                event.getVehicleId(), event.getGarageId(), event.getCreatedAt());
    }
}
