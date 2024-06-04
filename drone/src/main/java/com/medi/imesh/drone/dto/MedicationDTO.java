package com.medi.imesh.drone.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * DTO class for medication.
 */
public class MedicationDTO {

    private Long id;

    @NotBlank(message = "Medication name not found in the request.")
    @Pattern(regexp = "^[A-Za-z0-9\\-_]+$", message = "Name can only contain letters, numbers, '-' and '_'.")
    private String name;

    @NotBlank(message = "Medication code not found in the request.")
    @Pattern(regexp = "^[A-Z0-9_]+$", message = "Code can only contain uppercase letters, numbers, and '_'.")
    private String code;

    @Min(value = 1, message = "Weight limit must be at least 1g.")
    private int weight;

    @NotBlank(message = "Medication image not found in the request.")
    private String imageUrl;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
