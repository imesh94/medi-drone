package com.medi.imesh.drone.dto;

import com.medi.imesh.drone.common.ApplicationConstants;

/**
 * DTO used for updating the status of a drone.
 */
public class DroneStateUpdateDTO {
    private ApplicationConstants.DroneState newState;

    public ApplicationConstants.DroneState getNewState() {
        return newState;
    }

    public void setNewState(ApplicationConstants.DroneState newState) {
        this.newState = newState;
    }
}
