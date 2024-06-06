package com.GreenThumb.GT.models.CommunityGarden;

import com.GreenThumb.GT.models.CropsTracking.Crop;
import jakarta.persistence.*;
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

    private boolean plotNumber;

}
