package com.GreenThumb.GT.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class CommunityGarden {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String location;

    @NotBlank
    private String description;
    @NotBlank
    private int availablePlots;



    private int establishmentYear;

    private String socialMediaLinks;


    private String rulesAndRegulations;



    public CommunityGarden(String name, String location, String description, int availablePlots) {
        this.name = name;
        this.location = location;
        this.description = description;
        this.availablePlots = availablePlots;
    }
}
