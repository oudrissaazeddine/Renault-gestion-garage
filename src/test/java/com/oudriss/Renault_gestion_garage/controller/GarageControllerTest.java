package com.oudriss.Renault_gestion_garage.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oudriss.Renault_gestion_garage.dto.GarageRequest;
import com.oudriss.Renault_gestion_garage.dto.GarageResponse;
import com.oudriss.Renault_gestion_garage.dto.OpeningTimeDto;
import com.oudriss.Renault_gestion_garage.dto.PageResponse;
import com.oudriss.Renault_gestion_garage.exception.ResourceNotFoundException;
import com.oudriss.Renault_gestion_garage.service.GarageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GarageController.class)
class GarageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private GarageService garageService;

    private GarageRequest validRequest;
    private GarageResponse sampleResponse;

    @BeforeEach
    void setUp() {
        OpeningTimeDto openingTime = new OpeningTimeDto();
        openingTime.setStartTime(LocalTime.of(8, 0));
        openingTime.setEndTime(LocalTime.of(18, 0));

        validRequest = new GarageRequest();
        validRequest.setName("Garage Test");
        validRequest.setAddress("10 Rue Test");
        validRequest.setTelephone("0123456789");
        validRequest.setEmail("test@garage.fr");
        validRequest.setHorairesOuverture(Map.of(DayOfWeek.MONDAY, List.of(openingTime)));

        sampleResponse = new GarageResponse();
        sampleResponse.setId(1L);
        sampleResponse.setName("Garage Test");
        sampleResponse.setAddress("10 Rue Test");
        sampleResponse.setTelephone("0123456789");
        sampleResponse.setEmail("test@garage.fr");
    }

    @Test
    void createGarage_shouldReturn201_whenValidRequest() throws Exception {
        when(garageService.createGarage(any(GarageRequest.class))).thenReturn(sampleResponse);

        mockMvc.perform(post("/api/v1/garages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Garage Test"));
    }

    @Test
    void createGarage_shouldReturn400_whenMissingName() throws Exception {
        validRequest.setName("");

        mockMvc.perform(post("/api/v1/garages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createGarage_shouldReturn400_whenInvalidEmail() throws Exception {
        validRequest.setEmail("not-an-email");

        mockMvc.perform(post("/api/v1/garages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getGarage_shouldReturn200_whenExists() throws Exception {
        when(garageService.getGarageById(1L)).thenReturn(sampleResponse);

        mockMvc.perform(get("/api/v1/garages/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Garage Test"));
    }

    @Test
    void getGarage_shouldReturn404_whenNotExists() throws Exception {
        when(garageService.getGarageById(99L))
                .thenThrow(new ResourceNotFoundException("Garage introuvable avec l'ID: 99"));

        mockMvc.perform(get("/api/v1/garages/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllGarages_shouldReturn200_withPaginatedList() throws Exception {
        PageResponse<GarageResponse> pageResponse = new PageResponse<>(
                List.of(sampleResponse), 0, 10, 1, 1, true);
        when(garageService.getAllGarages(0, 10, "name", "asc")).thenReturn(pageResponse);

        mockMvc.perform(get("/api/v1/garages")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].name").value("Garage Test"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void deleteGarage_shouldReturn204_whenExists() throws Exception {
        doNothing().when(garageService).deleteGarage(1L);

        mockMvc.perform(delete("/api/v1/garages/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void updateGarage_shouldReturn200_whenValidRequest() throws Exception {
        when(garageService.updateGarage(eq(1L), any(GarageRequest.class))).thenReturn(sampleResponse);

        mockMvc.perform(put("/api/v1/garages/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }
}
