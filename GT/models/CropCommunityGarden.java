package com.GreenThumb.GT.models;

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
public class CropCommunityGarden {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "community_garden_id")
    private CommunityGarden communityGarden;

    @ManyToOne
    @JoinColumn(name = "crop_id")
    private Crop crop;

    private int plotNumber;

    public CropCommunityGarden(CommunityGarden communityGarden, Crop crop, int plotNumber) {
        this.communityGarden = communityGarden;
        this.crop = crop;
        this.plotNumber = plotNumber;
    }
}
