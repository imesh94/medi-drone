package com.medi.imesh.drone.controller;

import com.medi.imesh.drone.dto.MedicationDTO;
import com.medi.imesh.drone.service.MedicationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

/**
 * Controller for handling medication related requests.
 */
@RestController
@RequestMapping("/medications")
public class MedicationController {

    private final MedicationService medicationService;

    @Autowired
    public MedicationController(MedicationService medicationService) {
        this.medicationService = medicationService;
    }

    /**
     * Add a new medication to the system.
     *
     * @return Response with added medication details.
     */
    @PostMapping("/add")
    public ResponseEntity<MedicationDTO> addMedication(@Valid @RequestBody MedicationDTO medicationDTO) {

        MedicationDTO savedMedication = medicationService.saveMedication(medicationDTO);
        return ResponseEntity.ok(savedMedication);
    }

    /**
     * Get all medications registered in the system.
     *
     * @return A list of all medications.
     */
    @GetMapping
    public ResponseEntity<List<MedicationDTO>> getAllMedications() {

        List<MedicationDTO> medicationDTOs = medicationService.findAllMedications();
        return ResponseEntity.ok(medicationDTOs);
    }

    /**
     * Get details of a specific medication.
     *
     * @return Medication details.
     */
    @GetMapping("/{id}")
    public ResponseEntity<MedicationDTO> getMedicationById(@PathVariable Long id) {

        Optional<MedicationDTO> medicationDTOOpt = medicationService.findMedicationById(id);

        if (medicationDTOOpt.isPresent()) {
            return ResponseEntity.ok(medicationDTOOpt.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Deletes a medication identified by its ID.
     *
     * @param id The id of the medication to be deleted
     * @return a ResponseEntity with 204 No Content if deleted
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMedication(@PathVariable Long id) {

        boolean isDeleted = medicationService.deleteMedication(id);
        if (isDeleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
