package com.medi.imesh.drone.service;

import com.medi.imesh.drone.repository.MedicationRepository;
import com.medi.imesh.drone.dto.MedicationDTO;
import com.medi.imesh.drone.mapper.MedicationMapper;
import com.medi.imesh.drone.model.Medication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service class for managing medications.
 * Handles operations such as saving new medications and fetching medications by their properties.
 */
@Service
public class MedicationService {

    private final MedicationValidationService medicationValidationService;
    private final MedicationRepository medicationRepository;
    private static final Logger logger = LoggerFactory.getLogger(MedicationService.class);

    @Autowired
    public MedicationService(MedicationValidationService medicationValidationService,
                             MedicationRepository medicationRepository) {

        this.medicationValidationService = medicationValidationService;
        this.medicationRepository = medicationRepository;
    }

    /**
     * Saves a new medication entity in the database.
     *
     * @param medicationDTO The medication to save.
     * @return The saved medication, now with an ID assigned.
     */
    public MedicationDTO saveMedication(MedicationDTO medicationDTO) {

        Medication medication = MedicationMapper.dtoToEntity(medicationDTO);
        Medication savedMedication = medicationRepository.save(medication);
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Saved medication with ID %d in the system", savedMedication.getId()));
        }
        return MedicationMapper.entityToDto(savedMedication);
    }

    /**
     * Finds a medication by its unique ID.
     *
     * @param id The ID of the medication to find.
     * @return An Optional containing the found medication, or empty if no medication is found.
     */
    public Optional<MedicationDTO> findMedicationById(Long id) {

        Optional<Medication> medicationOpt = medicationRepository.findById(id);
        if (medicationOpt.isPresent()) {
            Medication medication = medicationOpt.get();
            MedicationDTO medicationDTO = MedicationMapper.entityToDto(medication);
            return Optional.of(medicationDTO);
        } else {
            return Optional.empty();
        }
    }

    /**
     * Retrieves all medications stored in the database.
     *
     * @return A List of all medications.
     */
    public List<MedicationDTO> findAllMedications() {

        List<Medication> medications = medicationRepository.findAll();
        List<MedicationDTO> medicationDTOs = new ArrayList<>();
        for (Medication medication : medications) {
            medicationDTOs.add(MedicationMapper.entityToDto(medication));
        }
        return medicationDTOs;
    }

    /**
     * Deletes a medication identified by its ID.
     *
     * @param id The ID of the medication to delete.
     * @return true if the medication was found and deleted, false if the medication was not found.
     */
    public boolean deleteMedication(Long id) {

        medicationValidationService.checkIfMedicationIsDeletable(id);
        if (medicationRepository.existsById(id)) {
            medicationRepository.deleteById(id);
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Deleted medication with ID %d from the system", id));
            }
            return true;
        } else {
            return false;
        }
    }
}
