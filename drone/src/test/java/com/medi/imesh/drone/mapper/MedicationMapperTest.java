package com.medi.imesh.drone.mapper;

import com.medi.imesh.drone.dto.MedicationDTO;
import com.medi.imesh.drone.model.Medication;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MedicationMapperTest {

    @Test
    public void givenMedicationDTO_whenDtoToEntity_thenCorrectMapping() {

        MedicationDTO medicationDTO = new MedicationDTO();
        medicationDTO.setId(1L);
        medicationDTO.setName("Aspirin");
        medicationDTO.setCode("ASP100");
        medicationDTO.setWeight(50);
        medicationDTO.setImageUrl("http://example.com/aspirin.jpg");

        Medication medication = MedicationMapper.dtoToEntity(medicationDTO);

        assertEquals(medicationDTO.getId(), medication.getId());
        assertEquals(medicationDTO.getName(), medication.getName());
        assertEquals(medicationDTO.getCode(), medication.getCode());
        assertEquals(medicationDTO.getWeight(), medication.getWeight());
        assertEquals(medicationDTO.getImageUrl(), medication.getImageUrl());
    }

    @Test
    public void givenMedicationEntity_whenEntityToDto_thenCorrectMapping() {

        Medication medication = new Medication();
        medication.setId(2L);
        medication.setName("Ibuprofen");
        medication.setCode("IBU200");
        medication.setWeight(100);
        medication.setImageUrl("http://example.com/ibuprofen.jpg");

        MedicationDTO medicationDTO = MedicationMapper.entityToDto(medication);

        assertEquals(medication.getId(), medicationDTO.getId());
        assertEquals(medication.getName(), medicationDTO.getName());
        assertEquals(medication.getCode(), medicationDTO.getCode());
        assertEquals(medication.getWeight(), medicationDTO.getWeight());
        assertEquals(medication.getImageUrl(), medicationDTO.getImageUrl());
    }
}
