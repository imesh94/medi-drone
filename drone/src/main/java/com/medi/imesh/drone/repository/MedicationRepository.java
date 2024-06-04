package com.medi.imesh.drone.repository;

import com.medi.imesh.drone.model.Medication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Medication instances.
 */
@Repository
public interface MedicationRepository extends JpaRepository<Medication, Long> {
}
