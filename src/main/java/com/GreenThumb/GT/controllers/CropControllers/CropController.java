package com.GreenThumb.GT.controllers.CropControllers;

import com.GreenThumb.GT.models.CropsTracking.Crop;
import com.GreenThumb.GT.services.CropsServices.CropService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/crops")
public class CropController {

    private CropService cropService;

    @Autowired
    public CropController(CropService cropService) {
        this.cropService = cropService;
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<Crop>> getAllCrops() {
        return ResponseEntity.ok(cropService.getAllCrops());
    }

/*
    @PostMapping("/add")
    public ResponseEntity<Crop> addCrop(@RequestBody Crop crop) {
        if (crop.getName() == null || crop.getName().isEmpty() ||
                crop.getDescription() == null || crop.getDescription().isEmpty() ||
                crop.getPlantingSeason() == null || crop.getPlantingSeason().isEmpty() ||
                crop.getType() == null || crop.getType().isEmpty() ||
                crop.getSunlightRequirement() == null || crop.getSunlightRequirement().isEmpty() ||
                crop.getSoilType() == null || crop.getSoilType().isEmpty() ||
                crop.getDaysToMaturity() <= 0 || crop.getPlantingDate() == null ||
                crop.getLocation() == null || crop.getLocation().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Crop savedCrop = cropService.saveCrop(crop);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCrop);
    }
*/
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCrop(@PathVariable("id") Long id) {
        cropService.deleteCrop(id);
        return ResponseEntity.noContent().build();
    }
}
