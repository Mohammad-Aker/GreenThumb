package com.GreenThumb.GT.controllers.CommunityGarden;
import com.GreenThumb.GT.models.CommunityGarden.CropCommunityGarden;
import com.GreenThumb.GT.services.CommunityGardenServices.CropCommunityGardenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cropCommunityGarden")
public class CropCommunityGardenController {

    @Autowired
    private CropCommunityGardenService cropCommunityGardenService;

    @PutMapping("/{id}")
    public CropCommunityGarden updatePlotNumber(@PathVariable Long id, @RequestParam boolean plotNumber) {
        return cropCommunityGardenService.updatePlotNumber(id, plotNumber);
    }
}
