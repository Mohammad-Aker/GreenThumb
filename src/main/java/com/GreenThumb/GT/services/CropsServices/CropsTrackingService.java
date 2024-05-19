package com.GreenThumb.GT.services.CropsServices;

import com.GreenThumb.GT.models.CropsTracking.CropsTracking;
import com.GreenThumb.GT.repositories.CropsRepositories.CropsTrackingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class CropsTrackingService {

    private CropsTrackingRepository cropsTrackingRepository;

    @Autowired
    public CropsTrackingService(CropsTrackingRepository cropsTrackingRepository) {
        this.cropsTrackingRepository = cropsTrackingRepository;
    }

    public List<CropsTracking> getAllCropsTracking() {
        return cropsTrackingRepository.findAll();
    }

    public Optional<CropsTracking> getCropsTrackingById(Long id) {
        return cropsTrackingRepository.findById(id);
    }

    public CropsTracking saveCropsTracking(CropsTracking cropsTracking) {
        return cropsTrackingRepository.save(cropsTracking);
    }

    public void deleteCropsTracking(Long id) {
        cropsTrackingRepository.deleteById(id);
    }

    public List<CropsTracking> getCropRotationsByUser(String userEmail) {
        return cropsTrackingRepository.findByUserEmail(userEmail);
    }




    //CORE FEATURE - 2 -

    // ******** User access to modify planting and harvesting Dates.
    public void schedulePlanting(String userEmail, Long cropId, Date plantingDate) {
        cropsTrackingRepository.schedulePlanting(userEmail, cropId, plantingDate);
    }

    public void recordActualPlanting(String userEmail, Long cropId, Date actualPlantingDate) {
        cropsTrackingRepository.recordActualPlanting(userEmail, cropId, actualPlantingDate);
    }

    public void scheduleHarvesting(String userEmail, Long cropId, Date harvestingDate) {
        cropsTrackingRepository.scheduleHarvest(userEmail, cropId, harvestingDate);
    }

    public void recordActualHarvesting(String userEmail, Long cropId, Date actualHarvestDate) {
        cropsTrackingRepository.recordActualHarvest(userEmail, cropId, actualHarvestDate);
    }



    // ******** Retrieving Crop rotations by using cropID + userEmail.
    public String findRotationNotesByUserEmailAndCropId(String userEmail, Long cropId) {
        return cropsTrackingRepository.findRotationNotesByUserEmailAndCropId(userEmail, cropId);
    }


    // ******** User crops harvested quantities
    public List<Object[]> getCropsAndHarvestedQuantityByUser(String userEmail) {
        return cropsTrackingRepository.findCropsAndHarvestedQuantityByUserEmail(userEmail);
    }

    // ******** Get location for crop
    public String getLocationForCrop(String userEmail, Long cropId) {
        CropsTracking cropsTracking = cropsTrackingRepository.findByUserEmailAndCropId(userEmail, cropId);
        if (cropsTracking != null) {
            return cropsTracking.getLocation();
        }
        return null;
    }
}
