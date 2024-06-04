package com.medi.imesh.drone.service;

import com.medi.imesh.drone.exception.ValidationException;
import com.medi.imesh.drone.repository.DroneMedicationRepository;
import com.medi.imesh.drone.repository.DroneRepository;
import com.medi.imesh.drone.common.ApplicationConstants;
import com.medi.imesh.drone.dto.DroneDTO;
import com.medi.imesh.drone.dto.MedicationDTO;
import com.medi.imesh.drone.model.Drone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service implementation for drone validation operations.
 */
@Service
public class DroneValidationServiceImpl implements DroneValidationService {

    @Autowired
    private DroneRepository droneRepository;
    @Autowired
    private MedicationService medicationService;
    @Autowired
    private DroneMedicationRepository droneMedicationRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public void validateDroneRegistration(DroneDTO droneDTO) throws ValidationException {

        String droneSerialNumber = droneDTO.getSerialNumber();

        // Check if the drone is already registered in the system.
        if (droneExistsInSystem(droneSerialNumber)) {
            throw new ValidationException(String.format("Drone with serial number %s already exists in the system.",
                    droneSerialNumber));
        }

        // Check if the battery level is not below the threshold if the drone is in LOADING state
        if (!ApplicationConstants.DroneState.IDLE.equals(droneDTO.getState())) {
            throw new ValidationException(String.format("Cannot register a drone with %s state. Initial drone state " +
                    "should be IDLE.", droneDTO.getState().toString()));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validateDroneLoadCapacity(String droneSerialNumber, Long medicationId) throws ValidationException {

        Drone drone = droneRepository.findBySerialNumber(droneSerialNumber)
                .orElseThrow(() -> new ValidationException(String.format("Drone with serial number %s does not " +
                        "exist in the system.", droneSerialNumber)));

        // Get existing weight of the drone
        int totalLoadedWeight = droneMedicationRepository.findByDroneSerialNumber(droneSerialNumber).stream()
                .mapToInt(dm -> dm.getMedication().getWeight()).sum();

        MedicationDTO medicationDTO = medicationService.findMedicationById(medicationId)
                .orElseThrow(() -> new ValidationException(String.format("Medication with ID %d does not exist in " +
                        "the system.", medicationId)));
        int newTotalWeight = totalLoadedWeight + medicationDTO.getWeight();

        if (newTotalWeight > drone.getWeightLimit()) {
            throw new ValidationException(String.format("Loading this medication would exceed the drone's weight " +
                            "limit. Current load: %d, Medication weight: %d, Weight limit: %d",
                    totalLoadedWeight, medicationDTO.getWeight(), drone.getWeightLimit()));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validateDroneStateChange(String droneSerialNumber, ApplicationConstants.DroneState newState)
            throws ValidationException {

        Drone drone = droneRepository.findBySerialNumber(droneSerialNumber)
                .orElseThrow(() -> new ValidationException(String.format("Drone with serial number %s does not " +
                        "exist in the system.", droneSerialNumber)));

        switch (newState) {
            case LOADING:
                validateTransitionToLoading(drone);
                break;
            case LOADED:
                validateTransitionToLoaded(drone);
                break;
            case DELIVERING:
                validateTransitionToDelivering(drone);
                break;
            case DELIVERED:
                validateTransitionToDelivered(drone);
                break;
            case RETURNING:
                validateTransitionToReturning(drone);
                break;
            case IDLE:
                validateTransitionToIdle(drone);
                break;
            default:
                break;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void checkIfDroneIsLoadable(String droneSerialNumber) throws ValidationException {

        if (!droneExistsInSystem(droneSerialNumber)) {
            throw new ValidationException(String.format("Drone with serial number %s does not exist in the system.",
                    droneSerialNumber));
        }

        if (!isDroneInLoadingState(droneSerialNumber)) {
            throw new ValidationException(String.format("Drone with serial number %s is not in LOADING state.",
                    droneSerialNumber));
        }

        if (isBatteryCapacityBelowThreshold(droneSerialNumber, ApplicationConstants.LOADING_BATTERY_THRESHOLD)) {
            throw new ValidationException(String.format("Drone with serial number %s has a battery capacity below %d.",
                    droneSerialNumber, ApplicationConstants.LOADING_BATTERY_THRESHOLD));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void checkDroneExists(String droneSerialNumber) throws ValidationException {

        if (!droneExistsInSystem(droneSerialNumber)) {
            throw new ValidationException(String.format("Drone with serial number %s does not exist in the system.",
                    droneSerialNumber));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void checkIfDroneIsDeletable(String droneSerialNumber) throws ValidationException {

        Drone drone = droneRepository.findBySerialNumber(droneSerialNumber)
                .orElseThrow(() -> new ValidationException(String.format("Drone with serial number %s does not " +
                        "exist in the system.", droneSerialNumber)));

        if (ApplicationConstants.DroneState.IDLE != drone.getState()) {
            throw new ValidationException(String.format("Drone with serial number %s cannot be deleted since it " +
                    "is not in IDLE state.", droneSerialNumber));
        }

        if (containsMedication(droneSerialNumber)) {
            throw new ValidationException(String.format("Drone with serial number %s cannot be deleted since it " +
                            "contains medication.", droneSerialNumber));
        }
    }

    /**
     * Checks if a drone exists in the system based on its serial number.
     *
     * @param droneSerialNumber the serial number of the drone to check
     * @return {@code true} if a drone with the specified serial number exists, otherwise {@code false}
     */
    private boolean droneExistsInSystem(String droneSerialNumber) {
        return droneRepository.findBySerialNumber(droneSerialNumber).isPresent();
    }

    /**
     * Checks if the specified drone is in the LOADING state.
     *
     * @param droneSerialNumber the serial number of the drone to check.
     * @return {@code true} if the drone is in the LOADING state, {@code false} otherwise.
     */
    private boolean isDroneInLoadingState(String droneSerialNumber) {

        Optional<Drone> droneOpt = droneRepository.findBySerialNumber(droneSerialNumber);
        if (droneOpt.isPresent()) {
            Drone drone = droneOpt.get();
            return ApplicationConstants.DroneState.LOADING.equals(drone.getState());
        }
        return false;
    }

    /**
     * Checks if a drone's battery capacity is below a specified threshold.
     *
     * @param droneSerialNumber the serial number of the drone to check.
     * @param threshold         the minimum battery capacity percentage required.
     * @return {@code true} if the drone's battery capacity is below the threshold, {@code false} otherwise.
     */
    private boolean isBatteryCapacityBelowThreshold(String droneSerialNumber, int threshold) {

        Optional<Drone> droneOpt = droneRepository.findBySerialNumber(droneSerialNumber);
        if (droneOpt.isPresent()) {
            Drone drone = droneOpt.get();
            return drone.getBatteryCapacity() < threshold;
        }
        return false;
    }

    /**
     * Ensures a drone is in IDLE or LOADED states and bettery capacity is not below the threshold  before
     * transitioning to LOADING.
     *
     * @param drone The drone in question.
     * @throws ValidationException If validation fails.
     */
    private void validateTransitionToLoading(Drone drone) throws ValidationException {

        String droneSerialNumber = drone.getSerialNumber();

        if (!(drone.getState() == ApplicationConstants.DroneState.IDLE ||
                drone.getState() == ApplicationConstants.DroneState.LOADED)) {
            throw new ValidationException("Drone can only transition to LOADING from IDLE or LOADED states.");
        }

        if (isBatteryCapacityBelowThreshold(droneSerialNumber, ApplicationConstants.LOADING_BATTERY_THRESHOLD)) {
            throw new ValidationException(String.format("Drone with serial number %s has a battery capacity below %d.",
                    droneSerialNumber, ApplicationConstants.LOADING_BATTERY_THRESHOLD));
        }
    }

    /**
     * Ensures a drone has medication loaded and current state is LOADING before transitioning to LOADED.
     *
     * @param drone The drone in question.
     * @throws ValidationException If no medication is loaded.
     */
    private void validateTransitionToLoaded(Drone drone) throws ValidationException {

        if (!containsMedication(drone.getSerialNumber())) {
            throw new ValidationException("Cannot change to LOADED state without medications.");
        }

        if (drone.getState() != ApplicationConstants.DroneState.LOADING) {
            throw new ValidationException("Drone must be in LOADING state to transition to LOADED.");
        }
    }

    /**
     * Validates transition to DELIVERING state from LOADED only.
     *
     * @param drone The drone in question.
     * @throws ValidationException If current state is not LOADED.
     */
    private void validateTransitionToDelivering(Drone drone) throws ValidationException {
        if (drone.getState() != ApplicationConstants.DroneState.LOADED) {
            throw new ValidationException("Drone must be in LOADED state to transition to DELIVERING.");
        }
    }

    /**
     * Validates transition to DELIVERED state from DELIVERING only.
     * Checks if all medications are unloaded before the state change to DELIVERED.
     *
     * @param drone The drone in question.
     * @throws ValidationException If validation fails.
     */
    private void validateTransitionToDelivered(Drone drone) throws ValidationException {

        if (drone.getState() != ApplicationConstants.DroneState.DELIVERING) {
            throw new ValidationException("Drone must be in DELIVERING state to transition to DELIVERED.");
        }

        if (containsMedication(drone.getSerialNumber())) {
            throw new ValidationException("Drone must have all medications unloaded before transitioning to " +
                    "DELIVERED state.");
        }
    }

    /**
     * Validates transition to RETURNING state from DELIVERED only.
     *
     * @param drone The drone in question.
     * @throws ValidationException If current state is not DELIVERED.
     */
    private void validateTransitionToReturning(Drone drone) throws ValidationException {
        if (drone.getState() != ApplicationConstants.DroneState.DELIVERED) {
            throw new ValidationException("Drone must be in DELIVERED state to transition to RETURNING.");
        }
    }

    /**
     * Validates transition to IDLE from LOADING, LOADED, or RETURNING.
     *
     * @param drone The drone in question.
     * @throws ValidationException If current state doesn't allow transition to IDLE.
     */
    private void validateTransitionToIdle(Drone drone) throws ValidationException {
        if (!(drone.getState() == ApplicationConstants.DroneState.LOADING ||
                drone.getState() == ApplicationConstants.DroneState.LOADED ||
                drone.getState() == ApplicationConstants.DroneState.RETURNING)) {
            throw new ValidationException("Drone can only transition to IDLE from LOADING, LOADED, or " +
                    "RETURNING states.");
        }
    }

    /**
     * Checks if a drone contains medication.
     *
     * @param droneSerialNumber The serial number of the drone to check.
     * @return True if there is medication in the drone, false otherwise.
     */
    private boolean containsMedication(String droneSerialNumber) {
        return !droneMedicationRepository.findByDroneSerialNumber(droneSerialNumber).isEmpty();
    }

}
