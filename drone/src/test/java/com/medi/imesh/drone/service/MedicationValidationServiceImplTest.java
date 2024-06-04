package com.medi.imesh.drone.service;

import com.medi.imesh.drone.exception.ValidationException;
import com.medi.imesh.drone.model.DroneMedication;
import com.medi.imesh.drone.model.Medication;
import com.medi.imesh.drone.repository.DroneMedicationRepository;
import com.medi.imesh.drone.repository.MedicationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MedicationValidationServiceImplTest {

    @Mock
    private MedicationRepository medicationRepository;

    @Mock
    private DroneMedicationRepository droneMedicationRepository;

    @InjectMocks
    private MedicationValidationServiceImpl medicationValidationService;

    @Test
    void checkIfMedicationIsDeletable_WhenMedicationDoesNotExist_ThrowsValidationException() {
        long medicationId = 1L;
        when(medicationRepository.findById(medicationId)).thenReturn(Optional.empty());

        assertThrows(ValidationException.class, () ->
                        medicationValidationService.checkIfMedicationIsDeletable(medicationId),
                "Expected ValidationException for non-existent medication.");
    }

    @Test
    void checkIfMedicationIsDeletable_WhenMedicationIsInUse_ThrowsValidationException() {
        long medicationId = 2L;
        Medication medication = new Medication();
        medication.setId(medicationId);
        when(medicationRepository.findById(medicationId)).thenReturn(Optional.of(medication));
        when(droneMedicationRepository.findByMedicationId(medicationId))
                .thenReturn(Collections.singletonList(new DroneMedication()));

        assertThrows(ValidationException.class, () ->
                        medicationValidationService.checkIfMedicationIsDeletable(medicationId),
                "Expected ValidationException when medication is in use.");
    }

    @Test
    void checkIfMedicationIsDeletable_WhenMedicationIsNotInUse_DoesNotThrowException() {
        long medicationId = 3L;
        Medication medication = new Medication();
        medication.setId(medicationId);
        when(medicationRepository.findById(medicationId)).thenReturn(Optional.of(medication));
        when(droneMedicationRepository.findByMedicationId(medicationId)).thenReturn(Collections.emptyList());

        medicationValidationService.checkIfMedicationIsDeletable(medicationId);
    }
}
