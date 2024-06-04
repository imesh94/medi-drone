package com.medi.imesh.drone.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.util.ArrayList;
import java.util.List;

/**
 * Model Class for Medication.
 */
@Entity
public class Medication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @OneToMany(mappedBy = "medication")
    private List<DroneMedication> droneMedications = new ArrayList<>();

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<DroneMedication> getDroneMedications() {
        return droneMedications;
    }

    public void setDroneMedications(List<DroneMedication> droneMedications) {
        this.droneMedications = droneMedications;
    }
}
