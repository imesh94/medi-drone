package com.medi.imesh.drone.service;

import com.medi.imesh.drone.common.ApplicationConstants;
import com.medi.imesh.drone.dto.DroneDTO;
import com.medi.imesh.drone.dto.MedicationDTO;
import com.medi.imesh.drone.exception.ValidationException;
import com.medi.imesh.drone.model.Drone;
import com.medi.imesh.drone.model.DroneMedication;
import com.medi.imesh.drone.model.Medication;
import com.medi.imesh.drone.repository.DroneMedicationRepository;
import com.medi.imesh.drone.repository.DroneRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DroneValidationServiceImplTest {

    @Mock
    private DroneRepository droneRepository;

    @Mock
    private MedicationService medicationService;

    @Mock
    private DroneMedicationRepository droneMedicationRepository;

    @InjectMocks
    private DroneValidationServiceImpl droneValidationService;

    private DroneDTO droneDTO;
    private Drone drone;
    private final String validSerialNumber = "VALID_SERIAL";
    private final String invalidSerialNumber = "INVALID_SERIAL";

    @BeforeEach
    void setUp() {

        droneDTO = new DroneDTO();
        droneDTO.setSerialNumber("DR001");

        drone = new Drone();
        drone.setSerialNumber(validSerialNumber);
        drone.setWeightLimit(500);
        drone.setBatteryCapacity(100);
        drone.setState(ApplicationConstants.DroneState.IDLE);
    }

    private Drone setupDroneWithState(ApplicationConstants.DroneState state) {

        Drone drone = new Drone();
        drone.setSerialNumber("DRONE123");
        drone.setState(state);
        drone.setBatteryCapacity(50);
        when(droneRepository.findBySerialNumber("DRONE123")).thenReturn(Optional.of(drone));
        return drone;
    }

    @Test
    void validateDroneRegistration_WhenDroneExists_ThrowsValidationException() {

        when(droneRepository.findBySerialNumber(droneDTO.getSerialNumber())).thenReturn(Optional.of(new Drone()));
        assertThrows(ValidationException.class, () -> droneValidationService.validateDroneRegistration(droneDTO),
                "Expected validateDroneRegistration to throw, but it didn't");
        verify(droneRepository).findBySerialNumber(droneDTO.getSerialNumber());
    }

    @Test
    void validateDroneRegistration_WhenDroneDoesNotExist_ProceedsNormally() {

        droneDTO.setState(ApplicationConstants.DroneState.IDLE);
        when(droneRepository.findBySerialNumber(droneDTO.getSerialNumber())).thenReturn(Optional.empty());
        assertDoesNotThrow(() -> droneValidationService.validateDroneRegistration(droneDTO),
                "Expected validateDroneRegistration to not throw, but it did");
        verify(droneRepository).findBySerialNumber(droneDTO.getSerialNumber());
    }

    @Test
    void validateDroneRegistration_WhenDroneInNonIdleState_thenThrowValidationException() {

        DroneDTO droneDTO = new DroneDTO();
        droneDTO.setState(ApplicationConstants.DroneState.LOADING);

        assertThrows(ValidationException.class,
                () -> droneValidationService.validateDroneRegistration(droneDTO),
                "Should throw ValidationException when registering a drone in LOADING state with " +
                        "non idle state.");
    }

    @Test
    void validateDroneLoadCapacity_WhenExceedsWeightLimit_ThrowsException() {

        String serialNumber = "SN002";
        long medicationId = 1L;

        Drone drone = new Drone();
        drone.setWeightLimit(100);

        MedicationDTO medicationDTO = new MedicationDTO();
        medicationDTO.setWeight(101);

        Medication mockMedication = new Medication();
        mockMedication.setWeight(50);

        DroneMedication droneMedication = new DroneMedication();
        droneMedication.setMedication(mockMedication);

        when(droneRepository.findBySerialNumber(serialNumber)).thenReturn(Optional.of(drone));
        when(medicationService.findMedicationById(medicationId)).thenReturn(Optional.of(medicationDTO));
        when(droneMedicationRepository.findByDroneSerialNumber(serialNumber))
                .thenReturn(Arrays.asList(droneMedication));

        assertThrows(ValidationException.class,
                () -> droneValidationService.validateDroneLoadCapacity(serialNumber, medicationId),
                "Expected validateDroneLoadCapacity to throw ValidationException, but it didn't");

        verify(droneRepository).findBySerialNumber(serialNumber);
        verify(medicationService).findMedicationById(medicationId);
        verify(droneMedicationRepository).findByDroneSerialNumber(serialNumber);
    }

    @Test
    void checkIfDroneIsLoadable_WhenDroneDoesNotExist_ThrowsValidationException() {

        when(droneRepository.findBySerialNumber(anyString())).thenReturn(Optional.empty());

        assertThrows(ValidationException.class, () -> droneValidationService.checkIfDroneIsLoadable("SN001"),
                "Expected checkIfDroneIsLoadable to throw ValidationException when drone does not exist, " +
                        "but it didn't");
    }

    @Test
    void checkIfDroneIsLoadable_WhenDroneNotInLoadingState_ThrowsValidationException() {

        Drone drone = new Drone();
        drone.setState(ApplicationConstants.DroneState.IDLE); // Not LOADING
        when(droneRepository.findBySerialNumber("SN001")).thenReturn(Optional.of(drone));

        assertThrows(ValidationException.class, () -> droneValidationService.checkIfDroneIsLoadable("SN001"),
                "Expected checkIfDroneIsLoadable to throw ValidationException when drone not in LOADING " +
                        "state, but it didn't");
    }

    @Test
    void checkIfDroneIsLoadable_WhenBatteryBelowThreshold_ThrowsValidationException() {

        Drone drone = new Drone();
        drone.setState(ApplicationConstants.DroneState.LOADING);
        drone.setBatteryCapacity(ApplicationConstants.LOADING_BATTERY_THRESHOLD - 1); // Below threshold
        when(droneRepository.findBySerialNumber("SN001")).thenReturn(Optional.of(drone));

        assertThrows(ValidationException.class, () -> droneValidationService.checkIfDroneIsLoadable("SN001"),
                "Expected checkIfDroneIsLoadable to throw ValidationException when battery below threshold, " +
                        "but it didn't");
    }

    @Test
    void checkIfDroneIsLoadable_WhenConditionsMet_DoesNotThrowException() {

        Drone drone = new Drone();
        drone.setState(ApplicationConstants.DroneState.LOADING);
        drone.setBatteryCapacity(ApplicationConstants.LOADING_BATTERY_THRESHOLD + 10); // Above threshold
        when(droneRepository.findBySerialNumber("SN001")).thenReturn(Optional.of(drone));

        assertDoesNotThrow(() -> droneValidationService.checkIfDroneIsLoadable("SN001"),
                "Expected checkIfDroneIsLoadable not to throw any exception when conditions are met, " +
                        "but it did");
    }

    @Test
    public void validateDroneStateChange_ToLoading_WhenDroneDoesNotExist_ThrowsValidationException() {

        when(droneRepository.findBySerialNumber(invalidSerialNumber)).thenReturn(Optional.empty());

        assertThrows(ValidationException.class,
                () -> droneValidationService.validateDroneStateChange(invalidSerialNumber,
                        ApplicationConstants.DroneState.LOADING), "Expected ValidationException when drone " +
                        "does not exist.");
    }

    @Test
    public void validateDroneStateChange_ToLoaded_WhenMedicationWeightIsZero_ThrowsValidationException() {

        when(droneRepository.findBySerialNumber(validSerialNumber)).thenReturn(Optional.of(drone));
        when(droneMedicationRepository.findByDroneSerialNumber(validSerialNumber)).thenReturn(Arrays.asList());

        assertThrows(ValidationException.class,
                () -> droneValidationService.validateDroneStateChange(validSerialNumber,
                        ApplicationConstants.DroneState.LOADED), "Expected ValidationException when trying " +
                        "to change state to LOADED without medications.");
    }

    @Test
    public void
    validateDroneStateChange_FromDeliveringToDelivered_WhenMedicationsAreStillLoaded_ThrowsValidationException() {

        Medication medication = new Medication();
        DroneMedication droneMedication = new DroneMedication();
        droneMedication.setDrone(drone);
        droneMedication.setMedication(medication);

        drone.setState(ApplicationConstants.DroneState.DELIVERING);

        when(droneRepository.findBySerialNumber(validSerialNumber)).thenReturn(Optional.of(drone));
        when(droneMedicationRepository.findByDroneSerialNumber(validSerialNumber)).thenReturn(Arrays.asList(
                droneMedication));

        assertThrows(ValidationException.class,
                () -> droneValidationService.validateDroneStateChange(validSerialNumber,
                        ApplicationConstants.DroneState.DELIVERED), "Expected ValidationException when " +
                        "trying to change state to DELIVERED while medications are still loaded.");
    }

    @Test
    public void validateDroneStateChange_FromLoadedToDelivering_WhenConditionsMet_DoesNotThrowException() {

        drone.setState(ApplicationConstants.DroneState.LOADED);

        when(droneRepository.findBySerialNumber(validSerialNumber)).thenReturn(Optional.of(drone));

        assertDoesNotThrow(() -> droneValidationService.validateDroneStateChange(validSerialNumber,
                ApplicationConstants.DroneState.DELIVERING), "State change from LOADED to " +
                "DELIVERING should not throw an exception if conditions are met.");
    }

    @Test
    void validateDroneStateChange_ToLoaded_WithoutMedication_ThrowsException() {

        Drone drone = setupDroneWithState(ApplicationConstants.DroneState.LOADING);
        when(droneMedicationRepository.findByDroneSerialNumber("DRONE123")).thenReturn(Collections.emptyList());

        assertThrows(ValidationException.class,
                () -> droneValidationService.validateDroneStateChange("DRONE123",
                        ApplicationConstants.DroneState.LOADED), "Drone cannot transition to LOADED state " +
                        "without medications.");
    }

    @Test
    void validateDroneStateChange_ToDelivering_NotFromLoaded_ThrowsException() {

        setupDroneWithState(ApplicationConstants.DroneState.IDLE);

        assertThrows(ValidationException.class,
                () -> droneValidationService.validateDroneStateChange("DRONE123",
                        ApplicationConstants.DroneState.DELIVERING), "Drone must be in LOADED state to " +
                        "transition to DELIVERING.");
    }

    @Test
    void validateDroneStateChange_ToDelivered_NotFromDelivering_ThrowsException() {

        setupDroneWithState(ApplicationConstants.DroneState.LOADING);

        assertThrows(ValidationException.class,
                () -> droneValidationService.validateDroneStateChange("DRONE123",
                        ApplicationConstants.DroneState.DELIVERED), "Drone must be in DELIVERING state to " +
                        "transition to DELIVERED.");
    }

    @Test
    void validateDroneStateChange_ToReturning_NotFromDelivered_ThrowsException() {

        setupDroneWithState(ApplicationConstants.DroneState.DELIVERING);

        assertThrows(ValidationException.class,
                () -> droneValidationService.validateDroneStateChange("DRONE123",
                        ApplicationConstants.DroneState.RETURNING), "Drone must be in DELIVERED state to " +
                        "transition to RETURNING.");
    }

    @Test
    void validateDroneStateChange_ToIdle_FromInvalidStates_ThrowsException() {

        setupDroneWithState(ApplicationConstants.DroneState.DELIVERING);

        assertThrows(ValidationException.class,
                () -> droneValidationService.validateDroneStateChange("DRONE123",
                        ApplicationConstants.DroneState.IDLE), "Drone can only transition to IDLE from " +
                        "LOADING, LOADED, or RETURNING states.");
    }

    @Test
    void validateDroneStateChange_ToIdle_FromValidStates_DoesNotThrowException() {

        Drone drone = setupDroneWithState(ApplicationConstants.DroneState.LOADING);
        assertDoesNotThrow(() -> droneValidationService.validateDroneStateChange("DRONE123",
                ApplicationConstants.DroneState.IDLE));

        drone.setState(ApplicationConstants.DroneState.LOADED);
        assertDoesNotThrow(() -> droneValidationService.validateDroneStateChange("DRONE123",
                ApplicationConstants.DroneState.IDLE));

        drone.setState(ApplicationConstants.DroneState.RETURNING);
        assertDoesNotThrow(() -> droneValidationService.validateDroneStateChange("DRONE123",
                ApplicationConstants.DroneState.IDLE));
    }

    @Test
    void validateDroneStateChange_ToLoaded_WithMedication_DoesNotThrowException() {

        Drone drone = setupDroneWithState(ApplicationConstants.DroneState.LOADING);
        DroneMedication droneMedication = new DroneMedication();
        when(droneMedicationRepository.findByDroneSerialNumber("DRONE123")).thenReturn(List.of(droneMedication));

        assertDoesNotThrow(() -> droneValidationService.validateDroneStateChange("DRONE123",
                ApplicationConstants.DroneState.LOADED), "Drone can transition to LOADED state with " +
                "medications.");
    }

    @Test
    void validateDroneStateChange_DroneDoesNotExist_ThrowsException() {

        when(droneRepository.findBySerialNumber("INVALID")).thenReturn(Optional.empty());

        assertThrows(ValidationException.class,
                () -> droneValidationService.validateDroneStateChange("INVALID",
                        ApplicationConstants.DroneState.IDLE), "Validation should fail if the drone does " +
                        "not exist.");
    }

    @Test
    void validateTransitionToDelivering_FromLoaded_DoesNotThrowException() {

        Drone drone = setupDroneWithState(ApplicationConstants.DroneState.LOADED);

        assertDoesNotThrow(() -> droneValidationService.validateDroneStateChange("DRONE123",
                ApplicationConstants.DroneState.DELIVERING), "Transition from LOADED to DELIVERING " +
                "should be valid.");
    }

    @Test
    void validateTransitionToDelivered_FromDelivering_DoesNotThrowException() {

        Drone drone = setupDroneWithState(ApplicationConstants.DroneState.DELIVERING);
        when(droneMedicationRepository.findByDroneSerialNumber("DRONE123")).thenReturn(Collections.emptyList());

        assertDoesNotThrow(() -> droneValidationService.validateDroneStateChange("DRONE123",
                ApplicationConstants.DroneState.DELIVERED), "Transition from DELIVERING to " +
                "DELIVERED should be valid when no medications are loaded.");
    }

    @Test
    void validateTransitionToDelivered_FromDelivering_WithMedicationsLoaded_ThrowsException() {

        Drone drone = setupDroneWithState(ApplicationConstants.DroneState.DELIVERING);
        DroneMedication droneMedication = new DroneMedication();
        when(droneMedicationRepository.findByDroneSerialNumber("DRONE123")).thenReturn(List.of(droneMedication));

        assertThrows(ValidationException.class,
                () -> droneValidationService.validateDroneStateChange("DRONE123",
                        ApplicationConstants.DroneState.DELIVERED), "Should throw ValidationException when " +
                        "attempting to transition to DELIVERED with medications still loaded.");
    }

    @Test
    void validateTransitionToReturning_FromDelivered_DoesNotThrowException() {

        Drone drone = setupDroneWithState(ApplicationConstants.DroneState.DELIVERED);

        assertDoesNotThrow(() -> droneValidationService.validateDroneStateChange("DRONE123",
                ApplicationConstants.DroneState.RETURNING), "Transition from DELIVERED to RETURNING " +
                "should be valid.");
    }

    @Test
    void validateTransitionToIdle_FromValidStates_DoesNotThrowException() {

        ApplicationConstants.DroneState[] validPreviousStates = {
                ApplicationConstants.DroneState.LOADING,
                ApplicationConstants.DroneState.LOADED,
                ApplicationConstants.DroneState.RETURNING};

        for (ApplicationConstants.DroneState validPreviousState : validPreviousStates) {
            Drone drone = setupDroneWithState(validPreviousState);

            assertDoesNotThrow(() -> droneValidationService.validateDroneStateChange("DRONE123",
                    ApplicationConstants.DroneState.IDLE), "Transition to IDLE should be valid from " +
                    validPreviousState);
        }
    }

    @Test
    void validateTransitionToIdle_FromInvalidStates_ThrowsException() {

        ApplicationConstants.DroneState[] invalidPreviousStates = {
                ApplicationConstants.DroneState.DELIVERING,
                ApplicationConstants.DroneState.DELIVERED};

        for (ApplicationConstants.DroneState invalidPreviousState : invalidPreviousStates) {
            Drone drone = setupDroneWithState(invalidPreviousState);

            assertThrows(ValidationException.class,
                    () -> droneValidationService.validateDroneStateChange("DRONE123",
                            ApplicationConstants.DroneState.IDLE), "Transition to IDLE should be invalid from "
                            + invalidPreviousState);
        }
    }

    @Test
    void checkIfDroneIsDeletable_DroneDoesNotExist_ThrowsValidationException() {

        when(droneRepository.findBySerialNumber("NON_EXISTENT_SERIAL")).thenReturn(Optional.empty());

        assertThrows(ValidationException.class,
                () -> droneValidationService.checkIfDroneIsDeletable("NON_EXISTENT_SERIAL"),
                "Expected ValidationException when drone does not exist.");
    }

    @Test
    void checkIfDroneIsDeletable_DroneNotInIDLEState_ThrowsValidationException() {

        Drone notIdleDrone = new Drone();
        notIdleDrone.setState(ApplicationConstants.DroneState.LOADED); // Not IDLE
        when(droneRepository.findBySerialNumber("DRONE123")).thenReturn(Optional.of(notIdleDrone));

        assertThrows(ValidationException.class,
                () -> droneValidationService.checkIfDroneIsDeletable("DRONE123"),
                "Expected ValidationException when drone not in IDLE state.");
    }

}
