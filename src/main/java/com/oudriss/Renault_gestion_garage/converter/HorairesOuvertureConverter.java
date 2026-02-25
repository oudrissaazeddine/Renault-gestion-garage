package com.oudriss.Renault_gestion_garage.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.oudriss.Renault_gestion_garage.entity.OpeningTime;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.DayOfWeek;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Converter
public class HorairesOuvertureConverter implements AttributeConverter<Map<DayOfWeek, List<OpeningTime>>, String> {

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Override
    public String convertToDatabaseColumn(Map<DayOfWeek, List<OpeningTime>> attribute) {
        if (attribute == null || attribute.isEmpty()) return null;
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Erreur conversion horaires → JSON", e);
        }
    }

    @Override
    public Map<DayOfWeek, List<OpeningTime>> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) return new HashMap<>();
        try {
            TypeReference<Map<DayOfWeek, List<OpeningTime>>> typeRef =
                    new TypeReference<>() {};
            return objectMapper.readValue(dbData, typeRef);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Erreur conversion JSON → horaires", e);
        }
    }
}
