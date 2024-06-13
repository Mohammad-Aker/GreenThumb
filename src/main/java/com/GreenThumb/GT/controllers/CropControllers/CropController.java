package com.GreenThumb.GT.controllers.CropControllers;

import com.GreenThumb.GT.models.CropsTracking.Crop;
import com.GreenThumb.GT.services.CropsServices.CropService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("GreenThumb/api/crops")
public class CropController {

    @Autowired
    private CropService cropService;

    @Autowired
    public CropController(CropService cropService) {
        this.cropService = cropService;
    }

    @PreAuthorize("hasAnyAuthority('USER', 'EXPERT','ADMIN')")
    @GetMapping("/get-all")
    public ResponseEntity<List<Crop>> getAllCrops() {
        return ResponseEntity.ok(cropService.getAllCrops());
    }


    @PostMapping("/add")
    @PreAuthorize("hasAnyAuthority('USER', 'EXPERT','ADMIN')")
    public ResponseEntity<Crop> addCrop(@RequestBody @Validated Crop crop) {
        if (crop.getDaysToMaturity() <= 0 || crop.getPlantingDate() == null) {
            return ResponseEntity.badRequest().build();
        }

        Crop savedCrop = cropService.saveCrop(crop);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCrop);
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('USER', 'EXPERT','ADMIN')")
    public ResponseEntity<Void> deleteCrop(@PathVariable("id") Long id) {
        cropService.deleteCrop(id);
        return ResponseEntity.noContent().build();
    }
}
