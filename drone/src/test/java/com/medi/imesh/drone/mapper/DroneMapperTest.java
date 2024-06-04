package com.medi.imesh.drone.mapper;

import com.medi.imesh.drone.common.ApplicationConstants;
import com.medi.imesh.drone.dto.DroneDTO;
import com.medi.imesh.drone.model.Drone;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DroneMapperTest {

    @Test
    public void givenDroneDTO_whenDtoToEntity_thenCorrectMapping() {

        DroneDTO droneDTO = new DroneDTO();
        droneDTO.setSerialNumber("DR001");
        droneDTO.setModel("Lightweight");
        droneDTO.setBatteryCapacity(75);
        droneDTO.setWeightLimit(100);
        droneDTO.setState(ApplicationConstants.DroneState.IDLE);

        Drone drone = DroneMapper.dtoToEntity(droneDTO);

        assertEquals(droneDTO.getSerialNumber(), drone.getSerialNumber());
        assertEquals(droneDTO.getModel(), drone.getModel());
        assertEquals(droneDTO.getBatteryCapacity(), drone.getBatteryCapacity());
        assertEquals(droneDTO.getWeightLimit(), drone.getWeightLimit());
        assertEquals(droneDTO.getState(), drone.getState());
    }

    @Test
    public void givenDroneEntity_whenEntityToDto_thenCorrectMapping() {

        Drone drone = new Drone();
        drone.setSerialNumber("DR002");
        drone.setModel("Middleweight");
        drone.setBatteryCapacity(85);
        drone.setWeightLimit(200);
        drone.setState(ApplicationConstants.DroneState.LOADING);

        DroneDTO droneDTO = DroneMapper.entityToDto(drone);

        assertEquals(drone.getSerialNumber(), droneDTO.getSerialNumber());
        assertEquals(drone.getModel(), droneDTO.getModel());
        assertEquals(drone.getBatteryCapacity(), droneDTO.getBatteryCapacity());
        assertEquals(drone.getWeightLimit(), droneDTO.getWeightLimit());
        assertEquals(drone.getState(), droneDTO.getState());
    }
}
