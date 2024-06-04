package com.medi.imesh.drone.controller;

import com.medi.imesh.drone.dto.DroneBatteryLevelUpdateDTO;
import com.medi.imesh.drone.dto.DroneDTO;
import com.medi.imesh.drone.dto.DroneStateUpdateDTO;
import com.medi.imesh.drone.dto.MedicationInfoDTO;
import com.medi.imesh.drone.service.DroneService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controller for handling drone related requests.
 */
@RestController
@RequestMapping("/drones")
public class DroneController {

    private final DroneService droneService;

    @Autowired
    public DroneController(DroneService droneService) {
        this.droneService = droneService;
    }

    /**
     * Register a new drone in the system.
     *
     * @return Response with registered drone details.
     */
    @PostMapping("/register")
    public ResponseEntity<DroneDTO> registerDrone(@Valid @RequestBody DroneDTO droneDTO) {
        DroneDTO registeredDroneDTO = droneService.registerDrone(droneDTO);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{serialNumber}")
                .buildAndExpand(registeredDroneDTO.getSerialNumber())
                .toUri();
        return ResponseEntity.created(location).body(registeredDroneDTO);
    }

    /**
     * Update the status of a drone.
     *
     * @return Response with success or error message.
     */
    @PutMapping("/{serialNumber}/state")
    public ResponseEntity<?> updateDroneState(@PathVariable String serialNumber,
                                              @Valid @RequestBody DroneStateUpdateDTO stateUpdateDTO) {
        droneService.updateDroneState(serialNumber, stateUpdateDTO.getNewState());
        return ResponseEntity.ok().body(Map.of("message", "Drone state updated successfully."));
    }

    /**
     * Get all drones registered in the system.
     *
     * @return A list of all drones.
     */
    @GetMapping
    public ResponseEntity<List<DroneDTO>> getAllDrones() {
        List<DroneDTO> dronesDTOList = new ArrayList<>(droneService.findAllDrones());
        return ResponseEntity.ok(dronesDTOList);
    }

    /**
     * Get details of a specific drone.
     *
     * @return Drone details.
     */
    @GetMapping("/{serialNumber}")
    public ResponseEntity<DroneDTO> getDroneBySerialNumber(@PathVariable String serialNumber) {
        Optional<DroneDTO> droneDTOOpt = droneService.findDroneBySerialNumber(serialNumber);
        if (droneDTOOpt.isPresent()) {
            return ResponseEntity.ok(droneDTOOpt.get());
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Get list of available drones for loading.
     *
     * @return A list of available drones for loading.
     */
    @GetMapping("/available")
    public ResponseEntity<List<DroneDTO>> getAvailableDrones() {
        List<DroneDTO> availableDronesDTOList = droneService.findAvailableDrones();
        return ResponseEntity.ok(availableDronesDTOList);
    }

    /**
     * Deletes a drone identified by its serial number.
     *
     * @param serialNumber the serial number of the drone to be deleted
     * @return a ResponseEntity with either a 204 No Content status if deleted
     */
    @DeleteMapping("/{serialNumber}")
    public ResponseEntity<Void> deleteDrone(@PathVariable String serialNumber) {
        boolean isDeleted = droneService.deleteDrone(serialNumber);
        if (isDeleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Load a given drone with medications.
     *
     * @param droneSerialNumber Serial number of the drone that should be loaded with medication
     * @param medicationId      Id of the medication to load
     * @return
     */
    @PostMapping("/{droneSerialNumber}/load/{medicationId}")
    public ResponseEntity<?> loadDroneWithMedication(@PathVariable String droneSerialNumber,
                                                     @PathVariable Long medicationId) {
        boolean isSuccess = droneService.loadDroneWithMedication(droneSerialNumber, medicationId);
        if (isSuccess) {
            return ResponseEntity.ok().body(Map.of("message", "Medication loaded successfully."));

        }
        return ResponseEntity.badRequest().body(Map.of("message", "Failed to load medication. Check drone's " +
                "capacity and battery level."));
    }

    /**
     * Get a detailed list of medications loaed in a given drone.
     *
     * @param droneSerialNumber Serial number of the drone
     * @return Detailed list of medications in the drone
     */
    @GetMapping("/{droneSerialNumber}/medications")
    public ResponseEntity<List<MedicationInfoDTO>> getLoadedMedications(@PathVariable String droneSerialNumber) {
        List<MedicationInfoDTO> medications = droneService.getLoadedMedicationsForDrone(droneSerialNumber);
        return ResponseEntity.ok(medications);
    }

    /**
     * Unloads all medications from a given drone.
     *
     * @param droneSerialNumber The serial number of the drone to unload.
     * @return ResponseEntity with success or error message.
     */
    @DeleteMapping("/{droneSerialNumber}/unloadAll")
    public ResponseEntity<?> unloadAllMedications(@PathVariable String droneSerialNumber) {

        boolean success = droneService.unloadAllMedicationsFromDrone(droneSerialNumber);
        if (success) {
            return ResponseEntity.ok().body(Map.of("message", "All medications unloaded successfully."));
        } else {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to unload medications or drone " +
                    "not found."));
        }
    }

    /**
     * Endpoint to retrieve the battery level of a drone by its serial number.
     *
     * @param serialNumber The serial number of the drone.
     * @return ResponseEntity containing the battery level or not found status.
     */
    @GetMapping("/{serialNumber}/batteryLevel")
    public ResponseEntity<?> getDroneBatteryLevel(@PathVariable String serialNumber) {
        Optional<Integer> batteryLevelOpt = droneService.getDroneBatteryLevel(serialNumber);

        if (batteryLevelOpt.isPresent()) {
            return ResponseEntity.ok(Map.of("batteryLevel", batteryLevelOpt.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Update the battery level of a drone.
     *
     * @param serialNumber          The serial number of the drone.
     * @param batteryLevelUpdateDTO A DTO containing the new battery level.
     * @return ResponseEntity with success or error message.
     */
    @PutMapping("/{serialNumber}/batteryLevel")
    public ResponseEntity<?> updateDroneBatteryLevel(@PathVariable String serialNumber,
                                                     @Valid @RequestBody DroneBatteryLevelUpdateDTO batteryLevelUpdateDTO) {
        droneService.updateDroneBatteryLevel(serialNumber, batteryLevelUpdateDTO.getBatteryLevel());
        return ResponseEntity.ok().body(Map.of("message", "Drone battery level updated successfully."));
    }

}
