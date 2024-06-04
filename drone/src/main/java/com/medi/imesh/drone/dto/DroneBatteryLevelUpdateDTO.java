package com.medi.imesh.drone.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * DTO class for updating battery level.
 */
public class DroneBatteryLevelUpdateDTO {

    @NotNull(message = "Battery level is required")
    @Min(value = 0, message = "Battery level must be at least 0%")
    @Max(value = 100, message = "Battery level cannot exceed 100%")
    private Integer batteryLevel;

    // Getters and Setters
    public Integer getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(Integer batteryLevel) {
        this.batteryLevel = batteryLevel;
    }
}
