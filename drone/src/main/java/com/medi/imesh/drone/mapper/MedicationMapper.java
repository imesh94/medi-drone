package com.medi.imesh.drone.mapper;

import com.medi.imesh.drone.dto.MedicationDTO;
import com.medi.imesh.drone.model.Medication;

/**
 * Mapper class for medication.
 * Contains mapping methods between medication DTO and medication model.
 */
public class MedicationMapper {

    private MedicationMapper() {
    }

    /**
     * Map DroneDTO to Medication entity
     *
     * @param medicationDTO Medication DTO
     * @return Mapped entity object
     */
    public static Medication dtoToEntity(MedicationDTO medicationDTO) {

        Medication medication = new Medication();
        medication.setId(medicationDTO.getId());
        medication.setName(medicationDTO.getName());
        medication.setCode(medicationDTO.getCode());
        medication.setWeight(medicationDTO.getWeight());
        medication.setImageUrl(medicationDTO.getImageUrl());
        return medication;
    }

    /**
     * Map Medication entity to MedicationDTO
     *
     * @param medication Medication entity
     * @return Mapped MedicationDTO
     */
    public static MedicationDTO entityToDto(Medication medication) {

        MedicationDTO medicationDTO = new MedicationDTO();
        medicationDTO.setId(medication.getId());
        medicationDTO.setName(medication.getName());
        medicationDTO.setCode(medication.getCode());
        medicationDTO.setWeight(medication.getWeight());
        medicationDTO.setImageUrl(medication.getImageUrl());
        return medicationDTO;
    }
}
