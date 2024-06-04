package com.medi.imesh.drone.service;

import com.medi.imesh.drone.exception.ValidationException;
import com.medi.imesh.drone.common.ApplicationConstants;
import com.medi.imesh.drone.dto.DroneDTO;


/**
 * Service interface for validating drone-related operations.
 * <p>
 * This interface provides methods for validating various aspects of drone
 * management.
 */
public interface DroneValidationService {

    /**
     * Validates the registration of a drone.
     *
     * @param droneDTO the drone data transfer object containing the drone information to validate
     * @throws ValidationException if validation fails
     */
    void validateDroneRegistration(DroneDTO droneDTO) throws ValidationException;

    /**
     * Validates the load capacity of the drone.
     *
     * @param droneSerialNumber Serial Number of the drone
     * @param medicationId      ID of the medication
     * @throws ValidationException if validation fails
     */
    void validateDroneLoadCapacity(String droneSerialNumber, Long medicationId) throws ValidationException;

    /**
     * Validates the legality of changing a drone's state.
     *
     * @param droneSerialNumber The drone's unique identifier.
     * @param newState          The intended new state for the drone.
     * @throws ValidationException If the state change is not allowed.
     */
    public void validateDroneStateChange(String droneSerialNumber, ApplicationConstants.DroneState newState)
            throws ValidationException;

    /**
     * Checks if the drone is loadable based on battery capacity and current state.
     *
     * @param droneSerialNumber Serial Number of the drone
     * @throws ValidationException if validation fails
     */
    void checkIfDroneIsLoadable(String droneSerialNumber) throws ValidationException;

    /**
     * Checks if the drone is loadable based on load and current state.
     *
     * @param droneSerialNumber Serial Number of the drone
     * @throws ValidationException if validation fails
     */
    void checkIfDroneIsDeletable(String droneSerialNumber) throws ValidationException;

    /**
     * Check if the drone exists in the system.
     *
     * @param droneSerialNumber Serial Number of the drone
     * @throws ValidationException if validation fails
     */
    void checkDroneExists(String droneSerialNumber) throws ValidationException;

}
