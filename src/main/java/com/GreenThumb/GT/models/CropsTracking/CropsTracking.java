package com.GreenThumb.GT.models.CropsTracking;

import com.GreenThumb.GT.models.User.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Entity
@NoArgsConstructor
public class CropsTracking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String userEmail;

    @ManyToOne
    @JoinColumn(name = "user_entity_email", referencedColumnName = "email", insertable = false, updatable = false)
    private User user;


    private Long cropId;

    @ManyToOne
    @JoinColumn(name = "crop_entity_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Crop crop;


    private String location;

    private String rotationNotes;

    private Date plannedPlantingDate;

    private Date actualPlantingDate;

    private Date plannedHarvestDate;

    private Date actualHarvestDate;

    private int harvestedQuantity;

    public CropsTracking(String userEmail, Long cropId, String rotationNotes, Date plannedPlantingDate, Date actualPlantingDate, Date plannedHarvestDate, Date actualHarvestDate, int harvestedQuantity, String location) {
        this.userEmail = userEmail;
        this.cropId = cropId;
        this.rotationNotes = rotationNotes;
        this.plannedPlantingDate = plannedPlantingDate;
        this.actualPlantingDate = actualPlantingDate;
        this.plannedHarvestDate = plannedHarvestDate;
        this.actualHarvestDate = actualHarvestDate;
        this.harvestedQuantity = harvestedQuantity;
        this.location=location;
    }
}
