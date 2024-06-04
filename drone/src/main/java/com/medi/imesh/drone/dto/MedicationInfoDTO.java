package com.medi.imesh.drone.dto;

/**
 * DTO class for retrieving the loaded medications in a drone.
 */
public class MedicationInfoDTO {

    private Long medicationId;
    private String medicationName;
    private int numberOfPacks;
    private int totalWeight;

    public MedicationInfoDTO(Long medicationId, String medicationName, int numberOfPacks, int totalWeight) {
        this.medicationId = medicationId;
        this.medicationName = medicationName;
        this.numberOfPacks = numberOfPacks;
        this.totalWeight = totalWeight;
    }

    public Long getMedicationId() {
        return medicationId;
    }

    public void setMedicationId(Long medicationId) {
        this.medicationId = medicationId;
    }

    public String getMedicationName() {
        return medicationName;
    }

    public void setMedicationName(String medicationName) {
        this.medicationName = medicationName;
    }

    public int getNumberOfPacks() {
        return numberOfPacks;
    }

    public void setNumberOfPacks(int numberOfPacks) {
        this.numberOfPacks = numberOfPacks;
    }

    public int getTotalWeight() {
        return totalWeight;
    }

    public void setTotalWeight(int totalWeight) {
        this.totalWeight = totalWeight;
    }
}
