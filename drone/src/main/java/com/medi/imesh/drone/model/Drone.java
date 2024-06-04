package com.medi.imesh.drone.model;

import com.medi.imesh.drone.common.ApplicationConstants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

/**
 * Model Class for Drone.
 */
@Entity
public class Drone {

    @Id
    @Column(name = "serial_number", length = 100)
    @NotBlank(message = "Serial number not found in the request.")
    @Size(max = 100, message = "Serial number cannot exceed 100 characters.")
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

    @Enumerated(EnumType.STRING)
    @NotNull(message = "State not found in the request.")
    private ApplicationConstants.DroneState state;

    @OneToMany(mappedBy = "drone")
    private List<DroneMedication> droneMedications = new ArrayList<>();

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

    public List<DroneMedication> getDroneMedications() {
        return droneMedications;
    }

    public void setDroneMedications(List<DroneMedication> droneMedications) {
        this.droneMedications = droneMedications;
    }

}
