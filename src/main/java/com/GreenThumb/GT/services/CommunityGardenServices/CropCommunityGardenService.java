package com.GreenThumb.GT.services.CommunityGardenServices;
import com.GreenThumb.GT.models.CommunityGarden.CommunityGarden;
import com.GreenThumb.GT.models.CommunityGarden.CropCommunityGarden;
import com.GreenThumb.GT.repositories.CommunityGardenRepository.CommunityGardenRepository;
import com.GreenThumb.GT.repositories.CommunityGardenRepository.CropCommunityGardenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CropCommunityGardenService {

    @Autowired
    private CropCommunityGardenRepository cropCommunityGardenRepository;

    @Autowired
    private CommunityGardenRepository communityGardenRepository;

    public CropCommunityGarden updatePlotNumber(Long cropCommunityGardenId, boolean plotNumber) {
        Optional<CropCommunityGarden> optionalCropCommunityGarden = cropCommunityGardenRepository.findById(cropCommunityGardenId);

        if (optionalCropCommunityGarden.isPresent()) {
            CropCommunityGarden cropCommunityGarden = optionalCropCommunityGarden.get();
            if (cropCommunityGarden.isPlotNumber() && !plotNumber) { // If plotNumber is being set to false
                CommunityGarden communityGarden = cropCommunityGarden.getCommunityGarden();
                communityGarden.setAvailablePlots(communityGarden.getAvailablePlots() - 1);
                communityGardenRepository.save(communityGarden);
            }
            cropCommunityGarden.setPlotNumber(plotNumber);
            return cropCommunityGardenRepository.save(cropCommunityGarden);
        } else {
            throw new IllegalArgumentException("CropCommunityGarden not found");
        }
    }
}
