package com.GreenThumb.GT.controllers.CropControllers;

import com.GreenThumb.GT.models.CropsTracking.CropsTracking;
import com.GreenThumb.GT.services.CropsServices.CropsTrackingService;
import com.GreenThumb.GT.services.ExternalAPIsServices.GeocodingService;
import com.GreenThumb.GT.services.ExternalAPIsServices.SoilDataService;
import com.GreenThumb.GT.services.ExternalAPIsServices.SoilService;
import com.GreenThumb.GT.services.ExternalAPIsServices.WeatherApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("GreenThumb/api/crops-tracking")
public class CropsTrackingController {

    private CropsTrackingService cropsTrackingService;
    private WeatherApiService weatherApiService;
    private SoilDataService soilDataService;
    private SoilService soilService;

    private GeocodingService geocodingService;


    @Autowired
    public CropsTrackingController(CropsTrackingService cropsTrackingService, WeatherApiService weatherApiService,SoilDataService soilDataService,GeocodingService geocodingService, SoilService soilService) {
        this.cropsTrackingService = cropsTrackingService;
        this.weatherApiService = weatherApiService;
        this.soilDataService=soilDataService;
        this.geocodingService=geocodingService;
        this.soilService=soilService;
    }

    @GetMapping("/get-all")
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

    @PostMapping("/create")
    public ResponseEntity<CropsTracking> saveCropsTracking(@RequestBody CropsTracking cropsTracking) {
        CropsTracking savedCropsTracking = cropsTrackingService.saveCropsTracking(cropsTracking);
        return new ResponseEntity<>(savedCropsTracking, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCropsTracking(@PathVariable Long id) {
        cropsTrackingService.deleteCropsTracking(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }



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



    @GetMapping("/rotations/{userEmail}/{cropId}")
    public ResponseEntity<String> getCropRotationNotes(@PathVariable String userEmail, @PathVariable Long cropId) {
        String rotationNotes = cropsTrackingService.findRotationNotesByUserEmailAndCropId(userEmail, cropId);
        if (rotationNotes != null) {
            return new ResponseEntity<>(rotationNotes, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Rotation notes not found", HttpStatus.NOT_FOUND);
        }
    }


    @GetMapping("/harvest-records/{userEmail}")
    public ResponseEntity<List<Object[]>> getCropsAndHarvestedQuantityByUser(@PathVariable String userEmail) {
        List<Object[]> cropsAndQuantities = cropsTrackingService.getCropsAndHarvestedQuantityByUser(userEmail);
        return new ResponseEntity<>(cropsAndQuantities, HttpStatus.OK);
    }



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




    @GetMapping("/geocode/{userEmail}/{cropId}")
    public ResponseEntity<Map<String, Float>> geocodeAddress(@PathVariable String userEmail, @PathVariable Long cropId) {
        try {
            String location = cropsTrackingService.getLocationForCrop(userEmail, cropId);

            if (location == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            Map<String, Float> coordinates = geocodingService.getGeocodingData(location);
            return new ResponseEntity<>(coordinates, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }




    @GetMapping("/soil/{userEmail}/{cropId}")
    public ResponseEntity<Map<String, Object>> getSoilData(@PathVariable String userEmail, @PathVariable Long cropId) {
        try {
            String location = cropsTrackingService.getLocationForCrop(userEmail, cropId);
            if (location == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            Map<String, Float> coordinates = geocodingService.getGeocodingData(location);
            float longitude = coordinates.get("lng");
            float latitude = coordinates.get("lat");
            Map<String, Object> soilData = soilDataService.getSoilData(longitude, latitude);
            return new ResponseEntity<>(soilData, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }







    @GetMapping("/soil/{userEmail}/{cropId}")
    public ResponseEntity<Map<String, Object>> getSoil(@PathVariable String userEmail, @PathVariable Long cropId) {
        try {
            String location = cropsTrackingService.getLocationForCrop(userEmail, cropId);

            if (location == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            Map<String, Float> coordinates = geocodingService.getGeocodingData(location);
            float longitude = coordinates.get("lng");
            float latitude = coordinates.get("lat");
            Map<String, Object> soilData = soilService.getSoilData(longitude, latitude);
            return new ResponseEntity<>(soilData, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
