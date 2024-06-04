package com.medi.imesh.drone.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medi.imesh.drone.dto.MedicationDTO;
import com.medi.imesh.drone.service.MedicationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(MedicationController.class)
public class MedicationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MedicationService medicationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void addMedication_WhenValidInput_ReturnsSavedMedication() throws Exception {
        MedicationDTO medicationDTO = new MedicationDTO();
        medicationDTO.setName("MedicineA");
        medicationDTO.setCode("MED123");
        medicationDTO.setWeight(100);
        medicationDTO.setImageUrl("medicineA.jpg");

        given(medicationService.saveMedication(any(MedicationDTO.class))).willReturn(medicationDTO);

        mockMvc.perform(post("/medications/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(medicationDTO)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(medicationDTO)));
    }

    @Test
    public void addMedication_WithInvalidName_ReturnsBadRequest() throws Exception {
        MedicationDTO medicationDTO = new MedicationDTO();
        medicationDTO.setName("Invalid#Name"); // This should violate the regex pattern
        medicationDTO.setCode("VALIDCODE123");

        mockMvc.perform(post("/medications/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(medicationDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addMedication_WithInvalidCode_ReturnsBadRequest() throws Exception {
        MedicationDTO medicationDTO = new MedicationDTO();
        medicationDTO.setName("ValidName");
        medicationDTO.setCode("invalid-code"); // Lowercase letters should violate the regex pattern

        mockMvc.perform(post("/medications/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(medicationDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getAllMedications_ReturnsListOfMedications() throws Exception {
        MedicationDTO medicationDTO1 = new MedicationDTO();
        MedicationDTO medicationDTO2 = new MedicationDTO();
        List<MedicationDTO> allMedications = Arrays.asList(medicationDTO1, medicationDTO2);

        given(medicationService.findAllMedications()).willReturn(allMedications);

        mockMvc.perform(get("/medications")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(allMedications)));
    }

    @Test
    public void getMedicationById_WhenFound_ReturnsMedication() throws Exception {
        Long medicationId = 1L;
        MedicationDTO medicationDTO = new MedicationDTO();
        medicationDTO.setId(medicationId);

        given(medicationService.findMedicationById(medicationId)).willReturn(Optional.of(medicationDTO));

        mockMvc.perform(get("/medications/{id}", medicationId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(medicationDTO)));
    }

    @Test
    public void getMedicationById_WhenNotFound_Returns404() throws Exception {
        Long medicationId = 1L;
        given(medicationService.findMedicationById(medicationId)).willReturn(Optional.empty());

        mockMvc.perform(get("/medications/{id}", medicationId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }


}