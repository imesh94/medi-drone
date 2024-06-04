package com.medi.imesh.drone.service;

import com.medi.imesh.drone.exception.ValidationException;
import com.medi.imesh.drone.repository.DroneMedicationRepository;
import com.medi.imesh.drone.repository.MedicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service implementation for medication validation operations.
 */
@Service
public class MedicationValidationServiceImpl implements MedicationValidationService {

    @Autowired
    private MedicationRepository medicationRepository;
    @Autowired
    private DroneMedicationRepository droneMedicationRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public void checkIfMedicationIsDeletable(Long medicationId) throws ValidationException {

        if (!medicationExistsInSystem(medicationId)) {
            throw new ValidationException(String.format("Medication with ID %d does not exist in the system.",
                    medicationId));
        }

        if (isMedicationInUse(medicationId)) {
            throw new ValidationException(String.format("Medication with ID %d is being transported in one of " +
                    "the drones and cannot be deleted.", medicationId));
        }
    }

    /**
     * Checks if a medication exists in the system based on its ID.
     *
     * @param medicationId the ID of the medication to check
     * @return {@code true} if a medication with the specified ID exists, otherwise {@code false}
     */
    private boolean medicationExistsInSystem(long medicationId) {
        return medicationRepository.findById(medicationId).isPresent();
    }

    /**
     * Checks if medication is contained in one of the drones.
     *
     * @param medicationId The id of the medication to check.
     * @return True if the medication is contained in one of the drones. False otherwise.
     */
    private boolean isMedicationInUse(long medicationId) {
        return !droneMedicationRepository.findByMedicationId(medicationId).isEmpty();
    }

}
