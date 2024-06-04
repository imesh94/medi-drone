package com.medi.imesh.drone.repository;

import com.medi.imesh.drone.model.DroneMedication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for medications loaded in drones.
 */
@Repository
public interface DroneMedicationRepository extends JpaRepository<DroneMedication, Long> {

    boolean existsByDroneSerialNumber(String droneSerialNumber);
    List<DroneMedication> findByDroneSerialNumber(String serialNumber);
    List<DroneMedication> findByMedicationId(long medicationId);
    void deleteAllByDroneSerialNumber(String droneSerialNumber);

}
