package com.GreenThumb.GT.controller;

import com.GreenThumb.GT.models.CropsTracking;
import com.GreenThumb.GT.services.CropsTrackingService;
import com.GreenThumb.GT.services.SoilDataService;
import com.GreenThumb.GT.services.WeatherApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/cropstracking")
public class CropsTrackingController {

    private CropsTrackingService cropsTrackingService;
    private WeatherApiService weatherApiService;
    private SoilDataService soilDataService;

    @Autowired
    public CropsTrackingController(CropsTrackingService cropsTrackingService, WeatherApiService weatherApiService,SoilDataService soilDataService) {
        this.cropsTrackingService = cropsTrackingService;
        this.weatherApiService = weatherApiService;
        this.soilDataService=soilDataService;
    }

    @GetMapping
    public ResponseEntity<List<CropsTracking>> getAllCropsTracking() {
        List<CropsTracking> cropsTrackings = cropsTrackingService.getAllCropsTracking();
        return new ResponseEntity<>(cropsTrackings, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CropsTracking> getCropsTrackingById(@PathVariable Long id) {
        return cropsTrackingService.getCropsTrackingById(id)
                .map(cropsTracking -> new ResponseEntity<>(cropsTracking, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<CropsTracking> saveCropsTracking(@RequestBody CropsTracking cropsTracking) {
        CropsTracking savedCropsTracking = cropsTrackingService.saveCropsTracking(cropsTracking);
        return new ResponseEntity<>(savedCropsTracking, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCropsTracking(@PathVariable Long id) {
        cropsTrackingService.deleteCropsTracking(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }





    //CORE FEATURE - 2 -
    //    ********   User access to modify planting and harvesting Dates.
    @PutMapping("/planting/schedule/{userEmail}/{cropId}")
    public void schedulePlanting(@PathVariable String userEmail, @PathVariable Long cropId, @RequestParam("plantingDate") @DateTimeFormat(pattern = "dd/MM/yyyy") Date plantingDate) {
        cropsTrackingService.schedulePlanting(userEmail, cropId, plantingDate);
    }


    @PutMapping("/planting/{userEmail}/{cropId}")
    public void recordActualPlanting(@PathVariable String userEmail, @PathVariable Long cropId, @RequestParam Date actualPlantingDate) {
      cropsTrackingService.recordActualPlanting(userEmail, cropId, actualPlantingDate);
    }


    @PutMapping("/harvest/schedule/{userEmail}/{cropId}")
    public void scheduleHarvesting(@PathVariable String userEmail, @PathVariable Long cropId, @RequestParam("harvestingDate") @DateTimeFormat(pattern = "dd/MM/yyyy") Date harvestingDate) {
        cropsTrackingService.scheduleHarvesting(userEmail, cropId, harvestingDate);
    }


    @PutMapping("/harvesting/{userEmail}/{cropId}")
    public void recordActualHarvesting(@PathVariable String userEmail, @PathVariable Long cropId, @RequestParam Date actualHarvestDate) {
        cropsTrackingService.recordActualHarvesting(userEmail, cropId, actualHarvestDate);
    }



    //    ********   Retrieving Crop rotations by using cropID + userEmail.            MORE DETAILS
    @GetMapping("/rotations/{userEmail}/{cropId}")
    public ResponseEntity<String> getCropRotationNotes(@PathVariable String userEmail, @PathVariable Long cropId) {
        String rotationNotes = cropsTrackingService.findRotationNotesByUserEmailAndCropId(userEmail, cropId);
        if (rotationNotes != null) {
            return new ResponseEntity<>(rotationNotes, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Rotation notes not found", HttpStatus.NOT_FOUND);
        }
    }


    //    ********   User crops harvested quantities
    @GetMapping("/harvest-records/{userEmail}")
    public ResponseEntity<List<Object[]>> getCropsAndHarvestedQuantityByUser(@PathVariable String userEmail) {
        List<Object[]> cropsAndQuantities = cropsTrackingService.getCropsAndHarvestedQuantityByUser(userEmail);
        return new ResponseEntity<>(cropsAndQuantities, HttpStatus.OK);
    }






    // EXTERNAL APIs Weeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee

    // ** WEATHER
    @GetMapping("/weather/{userEmail}/{cropId}")
    public ResponseEntity<String> getWeatherForCrop(@PathVariable String userEmail, @PathVariable Long cropId) {
        String location = cropsTrackingService.getLocationForCrop(userEmail, cropId);
        if (location != null) {
            try {
                String weatherData = weatherApiService.getWeatherData(location);
                return new ResponseEntity<>(weatherData, HttpStatus.OK);
            } catch (IOException e) {
                e.printStackTrace();
                return new ResponseEntity<>("Error occurred while fetching weather data", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new ResponseEntity<>("Crop location not found", HttpStatus.NOT_FOUND);
        }
    }



    /* T3BTTTTTTTTTTTTTTTTTTT
    @GetMapping("/soil/{category}")
    public ResponseEntity<String> getSoilDataForLocation(@PathVariable String category) {
        try {
            String soilData = soilDataService.getPlantCategoryData(category);
            return new ResponseEntity<>(soilData, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error occurred while fetching soil data", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
     */



}
