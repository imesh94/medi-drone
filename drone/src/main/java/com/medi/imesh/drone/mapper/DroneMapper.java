package com.medi.imesh.drone.mapper;

import com.medi.imesh.drone.dto.DroneDTO;
import com.medi.imesh.drone.model.Drone;

/**
 * Mapper class for drone.
 * Contains mapping methods between drone DTO and drone model.
 */
public class DroneMapper {

    private DroneMapper() {
    }

    /**
     * Map DroneDTO to Drone entity
     *
     * @param droneDTO Drone DTO
     * @return Mapped entity object
     */
    public static Drone dtoToEntity(DroneDTO droneDTO) {
        Drone drone = new Drone();
        drone.setSerialNumber(droneDTO.getSerialNumber());
        drone.setModel(droneDTO.getModel());
        drone.setBatteryCapacity(droneDTO.getBatteryCapacity());
        drone.setWeightLimit(droneDTO.getWeightLimit());
        drone.setState(droneDTO.getState());
        return drone;
    }

    /**
     * Map Drone entity to DroneDTO
     *
     * @param drone Drone entity
     * @return Mapped DroneDTO
     */
    public static DroneDTO entityToDto(Drone drone) {
        DroneDTO droneDTO = new DroneDTO();
        droneDTO.setSerialNumber(drone.getSerialNumber());
        droneDTO.setModel(drone.getModel());
        droneDTO.setBatteryCapacity(drone.getBatteryCapacity());
        droneDTO.setWeightLimit(drone.getWeightLimit());
        droneDTO.setState(drone.getState());
        return droneDTO;
    }

}
