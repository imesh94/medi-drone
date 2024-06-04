package com.medi.imesh.drone.service;

import com.medi.imesh.drone.repository.DroneMedicationRepository;
import com.medi.imesh.drone.repository.DroneRepository;
import com.medi.imesh.drone.common.ApplicationConstants;
import com.medi.imesh.drone.dto.DroneDTO;
import com.medi.imesh.drone.dto.MedicationDTO;
import com.medi.imesh.drone.dto.MedicationInfoDTO;
import com.medi.imesh.drone.mapper.DroneMapper;
import com.medi.imesh.drone.mapper.MedicationMapper;
import com.medi.imesh.drone.model.Drone;
import com.medi.imesh.drone.model.DroneMedication;
import com.medi.imesh.drone.model.Medication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for managing drones.
 * This includes registering drones, updating their state and battery levels,
 * and retrieving information about them.
 */
@Service
public class DroneService {

    private final DroneRepository droneRepository;
    private final DroneMedicationRepository droneMedicationRepository;
    private final DroneValidationService droneValidationService;
    private final MedicationService medicationService;
    private static final Logger logger = LoggerFactory.getLogger(DroneService.class);


    @Autowired
    public DroneService(DroneRepository droneRepository, DroneMedicationRepository droneMedicationRepository,
                        DroneValidationService droneValidationService, MedicationService medicationService) {

        this.droneRepository = droneRepository;
        this.droneMedicationRepository = droneMedicationRepository;
        this.droneValidationService = droneValidationService;
        this.medicationService = medicationService;
    }

    /**
     * Registers a new drone in the system.
     *
     * @param droneDTO The drone data transfer object to register.
     * @return The registered DroneDTO.
     */
    public DroneDTO registerDrone(DroneDTO droneDTO) {

        Drone drone = DroneMapper.dtoToEntity(droneDTO);
        droneValidationService.validateDroneRegistration(droneDTO);
        Drone savedDrone = droneRepository.save(drone);

        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Drone with serial number %s successfully registered in the system",
                    savedDrone.getSerialNumber()));
        }
        return DroneMapper.entityToDto(savedDrone);
    }

    /**
     * Retrieves all drones registered in the system.
     *
     * @return A List of DroneDTOs representing all drones.
     */
    public List<DroneDTO> findAllDrones() {

        List<Drone> drones = droneRepository.findAll();
        List<DroneDTO> droneDTOs = new ArrayList<>();
        for (Drone drone : drones) {
            droneDTOs.add(DroneMapper.entityToDto(drone));
        }
        return droneDTOs;
    }

    /**
     * Finds a drone by its Serial Number.
     *
     * @param droneSerialNumber The Serial Number of the drone to find.
     * @return An Optional containing the found DroneDTO, or empty if not found.
     */
    public Optional<DroneDTO> findDroneBySerialNumber(String droneSerialNumber) {

        Optional<Drone> optionalDrone = droneRepository.findBySerialNumber(droneSerialNumber);
        if (optionalDrone.isPresent()) {
            Drone drone = optionalDrone.get();
            DroneDTO droneDTO = DroneMapper.entityToDto(drone);
            return Optional.of(droneDTO);
        } else {
            return Optional.empty();
        }
    }

    /**
     * Finds drones available for loading based on battery capacity and state.
     * Drones are available if their battery capacity is at least 25%, and they are currently IDLE.
     *
     * @return A List of available DroneDTOs.
     */
    public List<DroneDTO> findAvailableDrones() {

        List<Drone> availableDrones = new ArrayList<>();
        List<Drone> allDrones = droneRepository.findAll();
        for (Drone drone : allDrones) {
            if (drone.getBatteryCapacity() >= 25 && drone.getState() == ApplicationConstants.DroneState.IDLE) {
                availableDrones.add(drone);
            }
        }
        return availableDrones.stream().map(DroneMapper::entityToDto).collect(Collectors.toList());
    }

    /**
     * Deletes a drone identified by its serial number.
     *
     * @param droneSerialNumber the serial number of the drone to be deleted
     * @return true if the drone was successfully deleted, false otherwise
     */
    public boolean deleteDrone(String droneSerialNumber) {

        droneValidationService.checkIfDroneIsDeletable(droneSerialNumber);
        Optional<Drone> drone = droneRepository.findBySerialNumber(droneSerialNumber);
        if (drone.isPresent()) {
            droneRepository.delete(drone.get());
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Drone with serial number %s successfully deleted from the system",
                        droneSerialNumber));
            }
            return true;
        }
        return false;
    }

    /**
     * Updates the state of a specific drone.
     *
     * @param droneSerialNumber The serial number of the drone to update.
     * @param newState          The new state to set for the drone.
     */
    public void updateDroneState(String droneSerialNumber, ApplicationConstants.DroneState newState) {

        droneValidationService.validateDroneStateChange(droneSerialNumber, newState);

        Optional<Drone> droneOpt = droneRepository.findBySerialNumber(droneSerialNumber);
        if (droneOpt.isPresent()) {
            Drone drone = droneOpt.get();
            ApplicationConstants.DroneState oldDroneState = drone.getState();
            drone.setState(newState);
            droneRepository.save(drone);

            if (logger.isDebugEnabled()) {
                logger.debug(String.format(String.format("Successfully changed the state of drone %s from %s to %s",
                        droneSerialNumber, oldDroneState.toString(), newState.toString())));
            }
        }
    }

    /**
     * Loads medication onto a specific drone.
     * Assumes checks for drone's capacity and battery level are done externally.
     *
     * @param droneSerialNumber The serial number of the drone to load.
     * @param medicationId      The ID of the medication to load onto the drone.
     * @return true if the medication is successfully loaded, false otherwise.
     */
    public boolean loadDroneWithMedication(String droneSerialNumber, Long medicationId) {

        droneValidationService.checkIfDroneIsLoadable(droneSerialNumber);
        droneValidationService.validateDroneLoadCapacity(droneSerialNumber, medicationId);

        Optional<Drone> droneOpt = droneRepository.findBySerialNumber(droneSerialNumber);
        Optional<MedicationDTO> medicationDTOOpt = medicationService.findMedicationById(medicationId);

        if (droneOpt.isPresent() && medicationDTOOpt.isPresent()) {
            Drone drone = droneOpt.get();
            MedicationDTO medicationDTO = medicationDTOOpt.get();

            // Create a new DroneMedication instance to represent this specific loading.
            DroneMedication droneMedication = new DroneMedication();
            droneMedication.setDrone(drone);
            droneMedication.setMedication(MedicationMapper.dtoToEntity(medicationDTO));

            droneMedicationRepository.save(droneMedication);
            return true;
        }
        return false;
    }

    /**
     * Retrieves the medications loaded on a specific drone.
     *
     * @param droneSerialNumber The serial number of the drone to query.
     * @return A list of medications loaded on the drone, or an empty list if none are found
     */
    public List<MedicationInfoDTO> getLoadedMedicationsForDrone(String droneSerialNumber) {

        droneValidationService.checkDroneExists(droneSerialNumber);

        List<DroneMedication> droneMedications = droneMedicationRepository.findByDroneSerialNumber(droneSerialNumber);

        // Use a Map to accumulate counts and weights by medicationId
        Map<Long, MedicationInfoDTO> medicationInfoMap = new HashMap<>();

        for (DroneMedication droneMedication : droneMedications) {
            Medication medication = droneMedication.getMedication();
            MedicationInfoDTO medicationInfoDTO = medicationInfoMap.getOrDefault(medication.getId(),
                    new MedicationInfoDTO(medication.getId(), medication.getName(), 0, 0));

            medicationInfoDTO.setNumberOfPacks(medicationInfoDTO.getNumberOfPacks() + 1);
            medicationInfoDTO.setTotalWeight(medicationInfoDTO.getTotalWeight() + medication.getWeight());

            medicationInfoMap.put(medication.getId(), medicationInfoDTO);
        }

        return new ArrayList<>(medicationInfoMap.values());
    }

    /**
     * Unloads all medications from a specific drone.
     *
     * @param droneSerialNumber The serial number of the drone to unload.
     * @return true if medications were successfully unloaded, false otherwise.
     */
    @Transactional
    public boolean unloadAllMedicationsFromDrone(String droneSerialNumber) {

        droneValidationService.checkDroneExists(droneSerialNumber);

        Optional<Drone> droneOptional = droneRepository.findBySerialNumber(droneSerialNumber);
        if (droneOptional.isPresent()) {
            droneMedicationRepository.deleteAllByDroneSerialNumber(droneSerialNumber);
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Unloaded all medications from the drone %s", droneOptional));
            }
            return true;
        }
        return false;
    }

    /**
     * Retrieves the battery level of a specific drone identified by its serial number.
     *
     * @param droneSerialNumber The serial number of the drone.
     * @return An Optional containing the battery level if the drone is found, or empty if not found.
     */
    public Optional<Integer> getDroneBatteryLevel(String droneSerialNumber) {

        droneValidationService.checkDroneExists(droneSerialNumber);

        Optional<Drone> droneOptional = droneRepository.findBySerialNumber(droneSerialNumber);
        if (droneOptional.isPresent()) {
            Drone drone = droneOptional.get();
            return Optional.of(drone.getBatteryCapacity());
        } else {
            return Optional.empty();
        }
    }

    /**
     * Updates the battery level of a specific drone.
     *
     * @param droneSerialNumber The serial number of the drone to update.
     * @param newBatteryLevel   The new battery level to set for the drone.
     */
    public void updateDroneBatteryLevel(String droneSerialNumber, int newBatteryLevel) {

        droneValidationService.checkDroneExists(droneSerialNumber);

        droneRepository.findBySerialNumber(droneSerialNumber).ifPresent(drone -> {
            drone.setBatteryCapacity(newBatteryLevel);
            droneRepository.save(drone);
        });
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Battery capacity of drone %s was updated to %d", droneSerialNumber,
                    newBatteryLevel));
        }
    }
}
