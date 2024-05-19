package com.GreenThumb.GT.models.CropsTracking;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Entity
@NoArgsConstructor
public class Crop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    private String imageUrl;

    private String plantingSeason;

    private String type; // vegetable, fruit, herb

    private String sunlightRequirement;

    private String soilType;

    private int daysToMaturity;

    private Date plantingDate;



    public Crop(String name, String description, String imageUrl, String plantingSeason, String type, String sunlightRequirement, String soilType, int daysToMaturity, Date plantingDate) {
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.plantingSeason = plantingSeason;
        this.type = type;
        this.sunlightRequirement = sunlightRequirement;
        this.soilType = soilType;
        this.daysToMaturity = daysToMaturity;
        this.plantingDate = plantingDate;
    }
}
