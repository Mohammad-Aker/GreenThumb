package com.GreenThumb.GT.repositories;

import com.GreenThumb.GT.models.CropsTracking;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;


@Repository
public interface CropsTrackingRepository extends JpaRepository<CropsTracking, Long> {

    List<CropsTracking> findByUserEmail(String userEmail);
    CropsTracking findByUserEmailAndCropId(String userEmail, Long cropId);






    //CORE FEATURE - 2 -
    //    ********   Retrieving Crop rotations bu using cropID + userEmail.

    @Query("SELECT ct.rotationNotes FROM CropsTracking ct WHERE ct.userEmail = ?1 AND ct.cropId = ?2")
    String findRotationNotesByUserEmailAndCropId(String userEmail, Long cropId);




    //    ********   User access to modify planting and harvesting Dates.

    @Modifying
    @Transactional
    @Query("UPDATE CropsTracking ct SET ct.plannedPlantingDate = :plantingDate WHERE ct.userEmail = :userEmail AND ct.cropId = :cropId")
    void schedulePlanting(String userEmail,Long cropId, Date plantingDate);

    @Modifying
    @Transactional
    @Query("UPDATE CropsTracking ct SET ct.actualPlantingDate = :actualPlantingDate WHERE ct.userEmail = :userEmail AND ct.cropId = :cropId")
    void recordActualPlanting(String userEmail,Long cropId, Date actualPlantingDate);

    @Modifying
    @Transactional
    @Query("UPDATE CropsTracking ct SET ct.plannedHarvestDate = :harvestingDate WHERE ct.userEmail = :userEmail AND ct.cropId = :cropId")
    void scheduleHarvest(String userEmail,Long cropId, Date harvestingDate);

    @Modifying
    @Transactional
    @Query("UPDATE CropsTracking ct SET ct.actualHarvestDate = :actualHarvestDate WHERE ct.userEmail = :userEmail AND ct.cropId = :cropId")
    void recordActualHarvest(String userEmail,Long cropId, Date actualHarvestDate);



    //    ********   User crops harvested quantities
    @Query("SELECT c.name, ct.harvestedQuantity " +
            "FROM Crop c, CropsTracking ct " +
            "WHERE c.id = ct.cropId AND ct.userEmail = ?1")
    List<Object[]> findCropsAndHarvestedQuantityByUserEmail(String userEmail);


}
