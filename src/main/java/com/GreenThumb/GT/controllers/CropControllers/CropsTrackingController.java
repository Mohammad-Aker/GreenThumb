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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("GreenThumb/api/crops-tracking")
public class CropsTrackingController {

    private final CropsTrackingService cropsTrackingService;
    private final WeatherApiService weatherApiService;
    private final SoilDataService soilDataService;
    private final GeocodingService geocodingService;
    private final SoilService soilService;


    @Autowired
    public CropsTrackingController(CropsTrackingService cropsTrackingService, WeatherApiService weatherApiService, SoilDataService soilDataService, GeocodingService geocodingService, SoilService soilService) {
        this.cropsTrackingService = cropsTrackingService;
        this.weatherApiService = weatherApiService;
        this.soilDataService = soilDataService;
        this.geocodingService = geocodingService;
        this.soilService = soilService;
    }

    @GetMapping("/get-all")
    @PreAuthorize("hasAnyAuthority('USER', 'EXPERT','ADMIN')")
    public ResponseEntity<List<CropsTracking>> getAllCropsTracking() {
        List<CropsTracking> cropsTrackings = cropsTrackingService.getAllCropsTracking();
        return new ResponseEntity<>(cropsTrackings, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('USER', 'EXPERT','ADMIN')")
    public ResponseEntity<CropsTracking> getCropsTrackingById(@PathVariable Long id) {
        return cropsTrackingService.getCropsTrackingById(id)
                .map(cropsTracking -> new ResponseEntity<>(cropsTracking, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyAuthority('USER', 'EXPERT','ADMIN')")
    public ResponseEntity<CropsTracking> saveCropsTracking(@RequestBody CropsTracking cropsTracking) {
        CropsTracking savedCropsTracking = cropsTrackingService.saveCropsTracking(cropsTracking);
        return new ResponseEntity<>(savedCropsTracking, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('USER', 'EXPERT','ADMIN')")
    public ResponseEntity<?> deleteCropsTracking(@PathVariable Long id) {
        cropsTrackingService.deleteCropsTracking(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/planting/schedule/{cropId}")
    @PreAuthorize("hasAnyAuthority('USER', 'EXPERT','ADMIN')")
    public void schedulePlanting(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long cropId, @RequestParam("plantingDate") @DateTimeFormat(pattern = "dd/MM/yyyy") Date plantingDate) {
        String userEmail = userDetails.getUsername(); // Retrieve authenticated user's email

        cropsTrackingService.schedulePlanting(userEmail, cropId, plantingDate);
    }

    @PutMapping("/planting/{cropId}")
    @PreAuthorize("hasAnyAuthority('USER', 'EXPERT','ADMIN')")
    public void recordActualPlanting(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long cropId, @RequestParam Date actualPlantingDate) {
        String userEmail = userDetails.getUsername();
        cropsTrackingService.recordActualPlanting(userEmail, cropId, actualPlantingDate);
    }


    @PutMapping("/harvest/schedule/{cropId}")
    @PreAuthorize("hasAnyAuthority('USER', 'EXPERT','ADMIN')")
    public void scheduleHarvesting(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long cropId, @RequestParam("harvestingDate") @DateTimeFormat(pattern = "dd/MM/yyyy") Date harvestingDate) {
        String userEmail = userDetails.getUsername();
        cropsTrackingService.scheduleHarvesting(userEmail, cropId, harvestingDate);
    }


    @PutMapping("/harvesting/{cropId}")
    @PreAuthorize("hasAnyAuthority('USER', 'EXPERT','ADMIN')")
    public void recordActualHarvesting(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long cropId, @RequestParam Date actualHarvestDate) {
        String userEmail = userDetails.getUsername(); // Retrieve authenticated user's email
        cropsTrackingService.recordActualHarvesting(userEmail, cropId, actualHarvestDate);
    }


    @GetMapping("/rotations/{cropId}")
    @PreAuthorize("hasAnyAuthority('USER', 'EXPERT','ADMIN')")
    public ResponseEntity<String> getCropRotationNotes(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long cropId) {
        String userEmail = userDetails.getUsername(); // Retrieve authenticated user's email
        String rotationNotes = cropsTrackingService.findRotationNotesByUserEmailAndCropId(userEmail, cropId);
        if (rotationNotes != null) {
            return new ResponseEntity<>(rotationNotes, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Rotation notes not found", HttpStatus.NOT_FOUND);
        }
    }


    @GetMapping("/harvest-records")
    @PreAuthorize("hasAnyAuthority('USER', 'EXPERT','ADMIN')")
    public ResponseEntity<List<Object[]>> getCropsAndHarvestedQuantityByUser(@AuthenticationPrincipal UserDetails userDetails) {
        String userEmail = userDetails.getUsername(); // Retrieve authenticated user's email
        List<Object[]> cropsAndQuantities = cropsTrackingService.getCropsAndHarvestedQuantityByUser(userEmail);
        return new ResponseEntity<>(cropsAndQuantities, HttpStatus.OK);
    }


    @GetMapping("/weather/{cropId}")
    @PreAuthorize("hasAnyAuthority('USER', 'EXPERT','ADMIN')")
    public ResponseEntity<String> getWeatherForCrop(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long cropId) {
        String userEmail = userDetails.getUsername(); // Retrieve authenticated user's email
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


    @GetMapping("/geocode/{cropId}")
    public ResponseEntity<Map<String, Float>> geocodeAddress(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long cropId) {
        String userEmail = userDetails.getUsername(); // Retrieve authenticated user's email
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

    @GetMapping("/soil-data/{cropId}")
    @PreAuthorize("hasAnyAuthority('USER', 'EXPERT','ADMIN')")
    public ResponseEntity<Map<String, Object>> getSoilData(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long cropId) {
        String userEmail = userDetails.getUsername(); // Retrieve authenticated user's email
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
    @PreAuthorize("hasAnyAuthority('USER', 'EXPERT','ADMIN')")
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
