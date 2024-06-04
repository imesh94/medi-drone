package com.medi.imesh.drone.service;

import com.medi.imesh.drone.dto.MedicationDTO;
import com.medi.imesh.drone.mapper.MedicationMapper;
import com.medi.imesh.drone.model.Medication;
import com.medi.imesh.drone.repository.MedicationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MedicationServiceTest {

    @Mock
    private MedicationRepository medicationRepository;

    @Mock
    private MedicationValidationService medicationValidationService;

    @InjectMocks
    private MedicationService medicationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Test saving a new medication.
     */
    @Test
    void saveMedication() {
        Medication medication = new Medication();
        medication.setName("Painkiller");
        when(medicationRepository.save(any(Medication.class))).thenReturn(medication);

        MedicationDTO medicationDTO = MedicationMapper.entityToDto(medication);

        MedicationDTO savedMedicationDTO = medicationService.saveMedication(medicationDTO);
        assertNotNull(savedMedicationDTO);
        assertEquals("Painkiller", savedMedicationDTO.getName());
    }

    /**
     * Test finding a medication by ID.
     */
    @Test
    void findMedicationById() {
        Medication medication = new Medication();
        medication.setId(1L);
        when(medicationRepository.findById(1L)).thenReturn(Optional.of(medication));

        Optional<MedicationDTO> foundMedication = medicationService.findMedicationById(1L);
        assertTrue(foundMedication.isPresent());
        assertEquals(1L, foundMedication.get().getId());
    }

    /**
     * Test retrieving all medications.
     */
    @Test
    void findAllMedications() {
        Medication med1 = new Medication();
        Medication med2 = new Medication();
        when(medicationRepository.findAll()).thenReturn(Arrays.asList(med1, med2));

        List<MedicationDTO> medications = medicationService.findAllMedications();
        assertNotNull(medications);
        assertEquals(2, medications.size());
    }

    @Test
    void deleteMedication_ExistingMedication() {
        Long medicationId = 1L;
        when(medicationRepository.existsById(medicationId)).thenReturn(true);

        boolean isDeleted = medicationService.deleteMedication(medicationId);

        assertTrue(isDeleted);
        verify(medicationRepository).deleteById(medicationId);
    }

    @Test
    void deleteMedication_NonExistingMedication() {
        Long medicationId = 2L;
        when(medicationRepository.existsById(medicationId)).thenReturn(false);

        boolean isDeleted = medicationService.deleteMedication(medicationId);

        assertFalse(isDeleted);
        verify(medicationRepository, never()).deleteById(medicationId);
    }
}