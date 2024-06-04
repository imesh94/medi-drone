package com.medi.imesh.drone.dto;

import com.medi.imesh.drone.common.ApplicationConstants;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO class for drone.
 */
public class DroneDTO {

    @NotBlank(message = "Serial number not found in the request.")
    @Size(max = 5, message = "Serial number cannot exceed 100 characters.")
    private String serialNumber;

    @NotBlank(message = "Model not found in the request.")
    @Pattern(regexp = "Lightweight|Middleweight|Cruiserweight|Heavyweight", message = "Invalid model. Should be one " +
            "of Lightweight, Middleweight, Cruiserweight, Heavyweight")
    private String model;

    @NotNull(message = "Weight limit not found in the request.")
    @Min(value = 1, message = "Weight limit must be at least 1g.")
    @Max(value = 500, message = "Weight limit cannot exceed 500g.")
    private int weightLimit;

    @NotNull(message = "Battery capacity not found in the request.")
    @Min(value = 1, message = "Battery capacity must be at least 1.")
    @Max(value = 100, message = "Battery capacity cannot exceed 100.")
    private int batteryCapacity;

    @NotNull(message = "State not found in the request.")
    private ApplicationConstants.DroneState state;

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getWeightLimit() {
        return weightLimit;
    }

    public void setWeightLimit(int weightLimit) {
        this.weightLimit = weightLimit;
    }

    public int getBatteryCapacity() {
        return batteryCapacity;
    }

    public void setBatteryCapacity(int batteryCapacity) {
        this.batteryCapacity = batteryCapacity;
    }

    public ApplicationConstants.DroneState getState() {
        return state;
    }

    public void setState(ApplicationConstants.DroneState state) {
        this.state = state;
    }

}

