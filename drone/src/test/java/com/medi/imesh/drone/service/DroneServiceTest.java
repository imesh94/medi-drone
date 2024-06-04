package com.medi.imesh.drone.service;

import com.medi.imesh.drone.common.ApplicationConstants;
import com.medi.imesh.drone.dto.DroneDTO;
import com.medi.imesh.drone.dto.MedicationInfoDTO;
import com.medi.imesh.drone.mapper.DroneMapper;
import com.medi.imesh.drone.model.Drone;
import com.medi.imesh.drone.model.DroneMedication;
import com.medi.imesh.drone.model.Medication;
import com.medi.imesh.drone.repository.DroneMedicationRepository;
import com.medi.imesh.drone.repository.DroneRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DroneServiceTest {

    @Mock
    private DroneRepository droneRepository;

    @Mock
    private DroneValidationService droneValidationService;

    @Mock
    private DroneMedicationRepository droneMedicationRepository;

    @InjectMocks
    private DroneService droneService;

    private AutoCloseable closeable;

    private MockedStatic<DroneMapper> mockedDroneMapper;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        mockedDroneMapper = Mockito.mockStatic(DroneMapper.class);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
        mockedDroneMapper.close();
    }

    @Test
    void registerDrone() {
        DroneDTO droneDTO = new DroneDTO();
        droneDTO.setSerialNumber("SN001");
        Drone drone = new Drone();
        drone.setSerialNumber("SN001");

        mockedDroneMapper.when(() -> DroneMapper.dtoToEntity(any(DroneDTO.class))).thenReturn(drone);
        mockedDroneMapper.when(() -> DroneMapper.entityToDto(any(Drone.class))).thenReturn(droneDTO);
        doNothing().when(droneValidationService).validateDroneRegistration(any(DroneDTO.class));

        when(droneRepository.save(any(Drone.class))).thenReturn(drone);

        DroneDTO savedDroneDTO = droneService.registerDrone(droneDTO);

        assertNotNull(savedDroneDTO);
        assertEquals("SN001", savedDroneDTO.getSerialNumber());
    }

    @Test
    void findAllDrones() {
        Drone drone1 = new Drone();
        Drone drone2 = new Drone();
        List<Drone> drones = Arrays.asList(drone1, drone2);
        DroneDTO droneDTO1 = new DroneDTO();
        DroneDTO droneDTO2 = new DroneDTO();

        mockedDroneMapper.when(() -> DroneMapper.entityToDto(any(Drone.class)))
                .thenReturn(droneDTO1, droneDTO2);
        when(droneRepository.findAll()).thenReturn(drones);

        List<DroneDTO> droneDTOs = droneService.findAllDrones();

        assertNotNull(droneDTOs);
        assertEquals(2, droneDTOs.size());
    }

    /**
     * Test finding a drone by its serial number and returning it as DroneDTO.
     */
    @Test
    void getDroneBySerialNumber() {
        Drone drone = new Drone();
        drone.setSerialNumber("SN002");
        DroneDTO droneDTO = new DroneDTO();
        droneDTO.setSerialNumber("SN002");

        mockedDroneMapper.when(() -> DroneMapper.entityToDto(any(Drone.class))).thenReturn(droneDTO);
        when(droneRepository.findBySerialNumber("SN002")).thenReturn(Optional.of(drone));

        Optional<DroneDTO> foundDroneDTO = droneService.findDroneBySerialNumber("SN002");

        assertTrue(foundDroneDTO.isPresent());
        assertEquals("SN002", foundDroneDTO.get().getSerialNumber());
    }

    @Test
    void deleteDrone_WhenExists_ReturnsTrue() {
        String serialNumber = "SN001";
        Drone drone = new Drone();
        drone.setSerialNumber(serialNumber);

        when(droneRepository.findBySerialNumber(serialNumber)).thenReturn(Optional.of(drone));

        boolean result = droneService.deleteDrone(serialNumber);

        assertTrue(result, "Drone should be successfully deleted");
        verify(droneRepository).delete(drone);
    }

    @Test
    void deleteDrone_WhenDoesNotExist_ReturnsFalse() {
        String serialNumber = "SN002";

        when(droneRepository.findBySerialNumber(serialNumber)).thenReturn(Optional.empty());

        boolean result = droneService.deleteDrone(serialNumber);

        assertFalse(result, "Drone should not be deleted because it does not exist");
        verify(droneRepository, never()).delete(any(Drone.class));
    }

    /**
     * Test updating the state of a drone.
     */
    @Test
    void updateDroneState() {
        Drone drone = new Drone();
        drone.setSerialNumber("D001");
        drone.setState(ApplicationConstants.DroneState.IDLE);

        when(droneRepository.findBySerialNumber("D001")).thenReturn(Optional.of(drone));
        droneService.updateDroneState("D001", ApplicationConstants.DroneState.LOADING);

        assertEquals(ApplicationConstants.DroneState.LOADING, drone.getState());
        verify(droneRepository, times(1)).save(drone);
    }

    /**
     * Test updating the battery level of a drone.
     */
    @Test
    void updateDroneBatteryLevel() {
        Drone drone = new Drone();
        drone.setSerialNumber("D001");
        drone.setBatteryCapacity(50);

        when(droneRepository.findBySerialNumber("D001")).thenReturn(Optional.of(drone));
        droneService.updateDroneBatteryLevel("D001", 75);

        assertEquals(75, drone.getBatteryCapacity());
        verify(droneRepository, times(1)).save(drone);
    }

    /**
     * Test finding available drones based on battery capacity and state.
     */
    @Test
    void findAvailableDrones() {
        Drone drone1 = new Drone();
        drone1.setBatteryCapacity(30);
        drone1.setState(ApplicationConstants.DroneState.IDLE);

        Drone drone2 = new Drone();
        drone2.setBatteryCapacity(20);
        drone2.setState(ApplicationConstants.DroneState.LOADING);

        DroneDTO droneDTO1 = new DroneDTO();
        droneDTO1.setBatteryCapacity(30);
        droneDTO1.setState(ApplicationConstants.DroneState.IDLE);

        List<Drone> drones = Arrays.asList(drone1, drone2);
        when(droneRepository.findAll()).thenReturn(drones);
        mockedDroneMapper.when(() -> DroneMapper.entityToDto(any(Drone.class))).thenReturn(droneDTO1);

        List<DroneDTO> availableDrones = droneService.findAvailableDrones();

        assertEquals(1, availableDrones.size());
        assertEquals(30, availableDrones.get(0).getBatteryCapacity());
        assertEquals(ApplicationConstants.DroneState.IDLE, availableDrones.get(0).getState());
    }

    @Test
    void getLoadedMedicationsForDrone_ReturnsCorrectAggregatedData() {
        String serialNumber = "SN001";
        Medication medication1 = new Medication();
        medication1.setId(1L);
        medication1.setName("MedicationA");
        medication1.setWeight(100);

        Medication medication2 = new Medication();
        medication2.setId(2L);
        medication2.setName("MedicationB");
        medication2.setWeight(150);

        // Simulate two packs of MedicationA and one pack of MedicationB loaded on the drone
        DroneMedication droneMedication1 = new DroneMedication();
        droneMedication1.setMedication(medication1);
        DroneMedication droneMedication2 = new DroneMedication();
        droneMedication2.setMedication(medication1); // Second pack of MedicationA
        DroneMedication droneMedication3 = new DroneMedication();
        droneMedication3.setMedication(medication2);

        when(droneMedicationRepository.findByDroneSerialNumber(serialNumber))
                .thenReturn(Arrays.asList(droneMedication1, droneMedication2, droneMedication3));

        List<MedicationInfoDTO> loadedMedications = droneService.getLoadedMedicationsForDrone(serialNumber);

        assertEquals(2, loadedMedications.size());

        MedicationInfoDTO medicationInfoA = loadedMedications.stream()
                .filter(m -> m.getMedicationId().equals(medication1.getId()))
                .findFirst()
                .orElseThrow(AssertionError::new);

        assertEquals("MedicationA", medicationInfoA.getMedicationName());
        assertEquals(2, medicationInfoA.getNumberOfPacks());
        assertEquals(200, medicationInfoA.getTotalWeight());

        MedicationInfoDTO medicationInfoB = loadedMedications.stream()
                .filter(m -> m.getMedicationId().equals(medication2.getId()))
                .findFirst()
                .orElseThrow(AssertionError::new);

        assertEquals("MedicationB", medicationInfoB.getMedicationName());
        assertEquals(1, medicationInfoB.getNumberOfPacks());
        assertEquals(150, medicationInfoB.getTotalWeight());
    }
}