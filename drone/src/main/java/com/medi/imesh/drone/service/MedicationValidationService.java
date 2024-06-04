package com.medi.imesh.drone.service;

import com.medi.imesh.drone.exception.ValidationException;


/**
 * Service interface for validating medication related operations.
 * <p>
 * This interface provides methods for validating various aspects of medication
 * management.
 */
public interface MedicationValidationService {

    /**
     * Check if medication is deletable based on its usage.
     *
     * @param medicationId ID of the medication
     * @throws ValidationException if validation fails
     */
    void checkIfMedicationIsDeletable(Long medicationId) throws ValidationException;

}
