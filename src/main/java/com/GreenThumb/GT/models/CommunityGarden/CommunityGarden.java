package com.GreenThumb.GT.models.CommunityGarden;

import jakarta.persistence.*;
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

    @Enumerated(EnumType.STRING)
    private GardenStatus status;

    public CommunityGarden(String name, String location, String description, int availablePlots) {
        this.name = name;
        this.location = location;
        this.description = description;
        this.availablePlots = availablePlots;
        this.status = GardenStatus.PENDING; // Default status
    }
}
